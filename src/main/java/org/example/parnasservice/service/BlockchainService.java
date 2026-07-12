package org.example.parnasservice.service;

import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.parnasservice.blockchain.contract.Campaign;
import org.example.parnasservice.blockchain.contract.CampaignFactory;
import org.example.parnasservice.config.BlockchainProperties;
import org.example.parnasservice.dto.response.EvmTransactionRequest;
import org.example.parnasservice.entity.BlockchainTransaction;
import org.example.parnasservice.entity.enums.TransactionStatus;
import org.example.parnasservice.entity.enums.TransactionType;
import org.example.parnasservice.exception.ConflictException;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Numeric;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockchainService {

    private final Web3j web3j;
    private final BlockchainProperties properties;

    private String deployedFactoryAddress;

    public void requireSupportedChainId(long chainId) {
        if (chainId != properties.getChainId()) {
            throw new IllegalArgumentException(
                "Unsupported chain id " + chainId + ". Expected local chain id " + properties.getChainId() + "."
            );
        }
    }

    public EvmTransactionRequest createCampaignTransaction(String from, String title, String description,
                                                           String targetAmountRaw, Instant deadline) {
        String factoryAddress = ensureFactoryAddress();
        Function function = new Function(
            "createCampaign",
            List.of(
                new Utf8String(title),
                new Utf8String(description),
                new Uint256(new BigInteger(targetAmountRaw)),
                new Uint256(BigInteger.valueOf(deadline.getEpochSecond()))
            ),
            List.of(TypeReference.create(Address.class))
        );
        log.info("Prepared campaign deployment transaction: from={} factory={} target={} deadline={}",
            from, factoryAddress, targetAmountRaw, deadline);
        return transaction(from, factoryAddress, FunctionEncoder.encode(function), "0");
    }

    public EvmTransactionRequest contributionTransaction(String from, String contractAddress, String amountRaw) {
        Function function = new Function("contribute", List.of(), List.of());
        log.info("Prepared contribution transaction: from={} contractAddress={} amount={}",
            from, contractAddress, amountRaw);
        return transaction(from, contractAddress, FunctionEncoder.encode(function), amountRaw);
    }

    public EvmTransactionRequest payoutTransaction(String from, String contractAddress, String profitAmountRaw) {
        Function function = new Function("distributeProfit", List.of(), List.of());
        log.info("Prepared payout transaction: from={} contractAddress={} amount={}",
            from, contractAddress, profitAmountRaw);
        return transaction(from, contractAddress, FunctionEncoder.encode(function), profitAmountRaw);
    }

    public TransactionReceipt requireSuccessfulReceipt(String transactionHash) {
        TransactionReceipt receipt = getReceipt(transactionHash);
        if (!receipt.isStatusOK()) {
            log.warn("Blockchain transaction reverted: txHash={} status={}", transactionHash, receipt.getStatus());
            throw new ConflictException("BLOCKCHAIN_TRANSACTION_FAILED", "Blockchain transaction was reverted.");
        }
        log.info("Blockchain receipt confirmed: txHash={} blockNumber={} gasUsed={}",
            receipt.getTransactionHash(), receipt.getBlockNumber(), receipt.getGasUsed());
        return receipt;
    }

    public Transaction requireTransaction(String transactionHash) {
        try {
            return web3j.ethGetTransactionByHash(transactionHash).send().getTransaction()
                .orElseThrow(() -> new ConflictException("TRANSACTION_NOT_FOUND", "Blockchain transaction was not found."));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read blockchain transaction.", e);
        }
    }

    public String requireCampaignCreated(TransactionReceipt receipt, String expectedOwner) {
        List<CampaignFactory.CampaignCreatedEventResponse> events =
            CampaignFactory.getCampaignCreatedEvents(receipt);
        for (CampaignFactory.CampaignCreatedEventResponse event : events) {
            if (!event.owner.equalsIgnoreCase(expectedOwner)) {
                log.warn("CampaignCreated owner mismatch: expectedOwner={} actualOwner={} campaign={}",
                    expectedOwner, event.owner, event.campaign);
                throw new ConflictException("BLOCKCHAIN_EVENT_MISMATCH", "Campaign owner in event does not match current user.");
            }
            log.info("CampaignCreated event found: campaign={} owner={}", event.campaign, event.owner);
            return event.campaign;
        }
        throw new ConflictException("BLOCKCHAIN_EVENT_NOT_FOUND", "CampaignCreated event was not found.");
    }

    public void requireContributionState(String contractAddress, String expectedContributor, String expectedAmount) {
        Campaign campaign = loadCampaign(contractAddress);
        try {
            BigInteger onChainAmount = campaign.contributions(expectedContributor).send();
            if (onChainAmount.compareTo(new BigInteger(expectedAmount)) >= 0) {
                log.info("Contribution state verified: contractAddress={} contributor={} onChainAmount={} expectedAmount={}",
                    contractAddress, expectedContributor, onChainAmount, expectedAmount);
                return;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read campaign contribution state.", e);
        }
        throw new ConflictException("BLOCKCHAIN_STATE_MISMATCH", "On-chain contribution state does not match prepared contribution.");
    }

    public void requireProfitDistributedState(String contractAddress, String expectedAmount) {
        Campaign campaign = loadCampaign(contractAddress);
        try {
            if (Boolean.TRUE.equals(campaign.profitDistributed().send())
                    && campaign.distributedProfit().send().equals(new BigInteger(expectedAmount))) {
                log.info("Payout state verified: contractAddress={} amount={}", contractAddress, expectedAmount);
                return;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Cannot read campaign payout state.", e);
        }
        throw new ConflictException("BLOCKCHAIN_STATE_MISMATCH", "On-chain payout state does not match prepared payout.");
    }

    public BlockchainTransaction toBlockchainTransaction(Transaction transaction, TransactionReceipt receipt,
                                                         TransactionType type, String valueRaw) {
        BlockchainTransaction tx = new BlockchainTransaction();
        tx.setHash(receipt.getTransactionHash());
        tx.setNetwork(properties.getNetwork());
        tx.setChainId((int) properties.getChainId());
        tx.setType(type);
        tx.setStatus(TransactionStatus.CONFIRMED);
        tx.setFrom(transaction.getFrom());
        tx.setTo(transaction.getTo());
        tx.setValue(valueRaw != null ? valueRaw : transaction.getValue().toString());
        tx.setBlockNumber(receipt.getBlockNumber().longValue());
        tx.setBlockHash(receipt.getBlockHash());
        tx.setConfirmations(confirmations(receipt.getBlockNumber()));
        tx.setRequiredConfirmations(properties.getRequiredConfirmations());
        tx.setGasUsed(receipt.getGasUsed() != null ? receipt.getGasUsed().toString() : null);
        tx.setCreatedAt(Instant.now());
        tx.setConfirmedAt(Instant.now());
        return tx;
    }

    private EvmTransactionRequest transaction(String from, String to, String data, String valueRaw) {
        return new EvmTransactionRequest(
            properties.getChainId(),
            from,
            to,
            data,
            valueRaw,
            properties.getGasLimit().toString(),
            null,
            null,
            Instant.now().plusSeconds(600)
        );
    }

    private synchronized String ensureFactoryAddress() {
        if (hasText(properties.getFactoryAddress())) {
            return properties.getFactoryAddress();
        }
        if (hasText(deployedFactoryAddress)) {
            return deployedFactoryAddress;
        }
        if (!hasText(properties.getDeployerPrivateKey())) {
            throw new IllegalStateException("Factory address is not configured and deployer private key is empty.");
        }
        log.info("CampaignFactory address is not configured; deploying factory via local signer.");
        deployedFactoryAddress = deployFactory();
        return deployedFactoryAddress;
    }

    private String deployFactory() {
        try {
            Credentials credentials = Credentials.create(normalizePrivateKey(properties.getDeployerPrivateKey()));
            BigInteger nonce = web3j.ethGetTransactionCount(
                    credentials.getAddress(), DefaultBlockParameterName.PENDING)
                .send().getTransactionCount();
            BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
            RawTransaction raw = RawTransaction.createContractTransaction(
                nonce,
                gasPrice != null ? gasPrice : DefaultGasProvider.GAS_PRICE,
                properties.getGasLimit(),
                BigInteger.ZERO,
                CampaignFactory.BINARY
            );
            byte[] signed = TransactionEncoder.signMessage(raw, properties.getChainId(), credentials);
            String txHash = web3j.ethSendRawTransaction(Numeric.toHexString(signed)).send().getTransactionHash();
            if (!hasText(txHash)) {
                throw new IllegalStateException("Factory deployment transaction was rejected.");
            }
            log.info("CampaignFactory deployment submitted: txHash={}", txHash);
            TransactionReceipt receipt = waitForReceipt(txHash);
            if (!receipt.isStatusOK() || !hasText(receipt.getContractAddress())) {
                throw new IllegalStateException("Factory deployment failed.");
            }
            log.info("CampaignFactory deployed: address={} txHash={}", receipt.getContractAddress(), txHash);
            return receipt.getContractAddress();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot deploy CampaignFactory.", e);
        }
    }

    private TransactionReceipt getReceipt(String transactionHash) {
        try {
            return web3j.ethGetTransactionReceipt(transactionHash).send().getTransactionReceipt()
                .orElseThrow(() -> new ConflictException("TRANSACTION_RECEIPT_NOT_FOUND", "Blockchain receipt was not found."));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read blockchain receipt.", e);
        }
    }

    private TransactionReceipt waitForReceipt(String transactionHash) throws IOException {
        for (int i = 0; i < 60; i++) {
            var receipt = web3j.ethGetTransactionReceipt(transactionHash).send().getTransactionReceipt();
            if (receipt.isPresent()) {
                return receipt.get();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while waiting for blockchain receipt.", e);
            }
        }
        throw new IllegalStateException("Timed out waiting for blockchain receipt.");
    }

    private int confirmations(BigInteger txBlock) {
        try {
            BigInteger latest = web3j.ethBlockNumber().send().getBlockNumber();
            return latest.subtract(txBlock).add(BigInteger.ONE).max(BigInteger.ZERO).intValue();
        } catch (IOException e) {
            return 0;
        }
    }

    private Campaign loadCampaign(String contractAddress) {
        return Campaign.load(
            contractAddress,
            web3j,
            new ReadonlyTransactionManager(web3j, contractAddress),
            new StaticGasProvider(DefaultGasProvider.GAS_PRICE, properties.getGasLimit())
        );
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private static String normalizePrivateKey(String value) {
        String normalized = Numeric.cleanHexPrefix(value == null ? "" : value.trim());
        if (!normalized.matches("(?i)[0-9a-f]{64}")) {
            throw new IllegalStateException("Blockchain deployer private key must contain exactly 64 hex characters.");
        }
        return normalized;
    }
}
