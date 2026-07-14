package org.example.parnasservice.blockchain.contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/LFDT-web3j/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.8.0.
 */
@SuppressWarnings("rawtypes")
@Generated("org.web3j.codegen.SolidityFunctionWrapperGenerator")
public class Campaign extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b50604051611fa6380380611fa6833981810160405281019061003291906102de565b846000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550836001908161008191906105a8565b50826002908161009191906105a8565b5081600381905550806004819055506000600760006101000a81548160ff021916908360028111156100c6576100c561067a565b5b021790555050505050506106a9565b6000604051905090565b600080fd5b600080fd5b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000610114826100e9565b9050919050565b61012481610109565b811461012f57600080fd5b50565b6000815190506101418161011b565b92915050565b600080fd5b600080fd5b6000601f19601f8301169050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b61019a82610151565b810181811067ffffffffffffffff821117156101b9576101b8610162565b5b80604052505050565b60006101cc6100d5565b90506101d88282610191565b919050565b600067ffffffffffffffff8211156101f8576101f7610162565b5b61020182610151565b9050602081019050919050565b60005b8381101561022c578082015181840152602081019050610211565b60008484015250505050565b600061024b610246846101dd565b6101c2565b9050828152602081018484840111156102675761026661014c565b5b61027284828561020e565b509392505050565b600082601f83011261028f5761028e610147565b5b815161029f848260208601610238565b91505092915050565b6000819050919050565b6102bb816102a8565b81146102c657600080fd5b50565b6000815190506102d8816102b2565b92915050565b600080600080600060a086880312156102fa576102f96100df565b5b600061030888828901610132565b955050602086015167ffffffffffffffff811115610329576103286100e4565b5b6103358882890161027a565b945050604086015167ffffffffffffffff811115610356576103556100e4565b5b6103628882890161027a565b9350506060610373888289016102c9565b9250506080610384888289016102c9565b9150509295509295909350565b600081519050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b600060028204905060018216806103e357607f821691505b6020821081036103f6576103f561039c565b5b50919050565b60008190508160005260206000209050919050565b60006020601f8301049050919050565b600082821b905092915050565b60006008830261045e7fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff82610421565b6104688683610421565b95508019841693508086168417925050509392505050565b6000819050919050565b60006104a56104a061049b846102a8565b610480565b6102a8565b9050919050565b6000819050919050565b6104bf8361048a565b6104d36104cb826104ac565b84845461042e565b825550505050565b600090565b6104e86104db565b6104f38184846104b6565b505050565b5b818110156105175761050c6000826104e0565b6001810190506104f9565b5050565b601f82111561055c5761052d816103fc565b61053684610411565b81016020851015610545578190505b61055961055185610411565b8301826104f8565b50505b505050565b600082821c905092915050565b600061057f60001984600802610561565b1980831691505092915050565b6000610598838361056e565b9150826002028217905092915050565b6105b182610391565b67ffffffffffffffff8111156105ca576105c9610162565b5b6105d482546103cb565b6105df82828561051b565b600060209050601f8311600181146106125760008415610600578287015190505b61060a858261058c565b865550610672565b601f198416610620866103fc565b60005b8281101561064857848901518255600182019150602085019450602081019050610623565b868310156106655784890151610661601f89168261056e565b8355505b6001600288020188555050505b505050505050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602160045260246000fd5b6118ee806106b86000396000f3fe6080604052600436106100fe5760003560e01c80637284e416116100955780638da5cb5b116100645780638da5cb5b1461031d578063953b8fb814610348578063b511b30c14610373578063d56bd1421461039e578063d7bb99ba146103c9576100fe565b80637284e4161461027f57806384bcefd4146102aa57806387d81789146102d55780638a160b5414610313576100fe565b806342e94c90116100d157806342e94c90146101c15780634e69d560146101fe5780635aa68ac0146102295780635c8f87c714610254576100fe565b806306fdde0314610103578063200d2ed21461012e57806329dcb0cf1461015957806335c1d34914610184575b600080fd5b34801561010f57600080fd5b506101186103d3565b6040516101259190610fe7565b60405180910390f35b34801561013a57600080fd5b50610143610461565b6040516101509190611080565b60405180910390f35b34801561016557600080fd5b5061016e610474565b60405161017b91906110b4565b60405180910390f35b34801561019057600080fd5b506101ab60048036038101906101a69190611100565b61047a565b6040516101b8919061116e565b60405180910390f35b3480156101cd57600080fd5b506101e860048036038101906101e391906111b5565b6104b9565b6040516101f591906110b4565b60405180910390f35b34801561020a57600080fd5b506102136104d1565b6040516102209190611080565b60405180910390f35b34801561023557600080fd5b5061023e61056c565b60405161024b91906112a0565b60405180910390f35b34801561026057600080fd5b506102696105fa565b60405161027691906110b4565b60405180910390f35b34801561028b57600080fd5b50610294610600565b6040516102a19190610fe7565b60405180910390f35b3480156102b657600080fd5b506102bf61068e565b6040516102cc91906110b4565b60405180910390f35b3480156102e157600080fd5b506102fc60048036038101906102f79190611100565b610694565b60405161030a9291906112c2565b60405180910390f35b61031b6106c8565b005b34801561032957600080fd5b50610332610aeb565b60405161033f919061116e565b60405180910390f35b34801561035457600080fd5b5061035d610b0f565b60405161036a91906110b4565b60405180910390f35b34801561037f57600080fd5b50610388610b15565b6040516103959190611306565b60405180910390f35b3480156103aa57600080fd5b506103b3610b28565b6040516103c0919061140e565b60405180910390f35b6103d1610b9b565b005b600180546103e09061145f565b80601f016020809104026020016040519081016040528092919081815260200182805461040c9061145f565b80156104595780601f1061042e57610100808354040283529160200191610459565b820191906000526020600020905b81548152906001019060200180831161043c57829003601f168201915b505050505081565b600760009054906101000a900460ff1681565b60045481565b6008818154811061048a57600080fd5b906000526020600020016000915054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60096020528060005260406000206000915090505481565b6000600454421180156105175750600060028111156104f3576104f2611009565b5b600760009054906101000a900460ff16600281111561051557610514611009565b5b145b80156105265750600354600554105b15610557576002600760006101000a81548160ff0219169083600281111561055157610550611009565b5b02179055505b600760009054906101000a900460ff16905090565b606060088054806020026020016040519081016040528092919081815260200182805480156105f057602002820191906000526020600020905b8160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190600101908083116105a6575b5050505050905090565b60065481565b6002805461060d9061145f565b80601f01602080910402602001604051908101604052809291908181526020018280546106399061145f565b80156106865780601f1061065b57610100808354040283529160200191610686565b820191906000526020600020905b81548152906001019060200180831161066957829003601f168201915b505050505081565b60055481565b600b81815481106106a457600080fd5b90600052602060002090600202016000915090508060000154908060010154905082565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614610756576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161074d906114dc565b60405180910390fd5b6004544211801561079a57506000600281111561077657610775611009565b5b600760009054906101000a900460ff16600281111561079857610797611009565b5b145b80156107a95750600354600554105b156107da576002600760006101000a81548160ff021916908360028111156107d4576107d3611009565b5b02179055505b600354600554101580156108215750600060028111156107fd576107fc611009565b5b600760009054906101000a900460ff16600281111561081f5761081e611009565b5b145b15610852576001600760006101000a81548160ff0219169083600281111561084c5761084b611009565b5b02179055505b6001600281111561086657610865611009565b5b600760009054906101000a900460ff16600281111561088857610887611009565b5b146108c8576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016108bf90611548565b60405180910390fd5b600760019054906101000a900460ff1615610918576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161090f906115b4565b60405180910390fd5b6000341161095b576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161095290611620565b60405180910390fd5b6001600760016101000a81548160ff021916908315150217905550346006819055506000600554905060005b600880549050811015610a91576000600882815481106109aa576109a9611640565b5b9060005260206000200160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff169050600083600960008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205434610a25919061169e565b610a2f919061170f565b90506000811115610a82578173ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f19350505050158015610a80573d6000803e3d6000fd5b505b50508080600101915050610987565b50600b60405180604001604052803481526020014281525090806001815401808255809150506001900390600052602060002090600202016000909190919091506000820151816000015560208201518160010155505050565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60035481565b600760019054906101000a900460ff1681565b6060600b805480602002602001604051908101604052809291908181526020016000905b82821015610b9257838290600052602060002090600202016040518060400160405290816000820154815260200160018201548152505081526020019060010190610b4c565b50505050905090565b60045442118015610bdf575060006002811115610bbb57610bba611009565b5b600760009054906101000a900460ff166002811115610bdd57610bdc611009565b5b145b8015610bee5750600354600554105b15610c1f576002600760006101000a81548160ff02191690836002811115610c1957610c18611009565b5b02179055505b60035460055410158015610c66575060006002811115610c4257610c41611009565b5b600760009054906101000a900460ff166002811115610c6457610c63611009565b5b145b15610c97576001600760006101000a81548160ff02191690836002811115610c9157610c90611009565b5b02179055505b60006002811115610cab57610caa611009565b5b600760009054906101000a900460ff166002811115610ccd57610ccc611009565b5b14610d0d576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610d049061178c565b60405180910390fd5b60003411610d50576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610d47906117f8565b60405180910390fd5b60035434600554610d619190611818565b1115610da2576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401610d9990611898565b60405180910390fd5b600a60003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060009054906101000a900460ff16610eaf576008339080600181540180825580915050600190039060005260206000200160009091909190916101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506001600a60003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060006101000a81548160ff0219169083151502179055505b34600960003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254610efe9190611818565b925050819055503460056000828254610f179190611818565b9250508190555060035460055410610f55576001600760006101000a81548160ff02191690836002811115610f4f57610f4e611009565b5b02179055505b565b600081519050919050565b600082825260208201905092915050565b60005b83811015610f91578082015181840152602081019050610f76565b60008484015250505050565b6000601f19601f8301169050919050565b6000610fb982610f57565b610fc38185610f62565b9350610fd3818560208601610f73565b610fdc81610f9d565b840191505092915050565b600060208201905081810360008301526110018184610fae565b905092915050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602160045260246000fd5b6003811061104957611048611009565b5b50565b600081905061105a82611038565b919050565b600061106a8261104c565b9050919050565b61107a8161105f565b82525050565b60006020820190506110956000830184611071565b92915050565b6000819050919050565b6110ae8161109b565b82525050565b60006020820190506110c960008301846110a5565b92915050565b600080fd5b6110dd8161109b565b81146110e857600080fd5b50565b6000813590506110fa816110d4565b92915050565b600060208284031215611116576111156110cf565b5b6000611124848285016110eb565b91505092915050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b60006111588261112d565b9050919050565b6111688161114d565b82525050565b6000602082019050611183600083018461115f565b92915050565b6111928161114d565b811461119d57600080fd5b50565b6000813590506111af81611189565b92915050565b6000602082840312156111cb576111ca6110cf565b5b60006111d9848285016111a0565b91505092915050565b600081519050919050565b600082825260208201905092915050565b6000819050602082019050919050565b6112178161114d565b82525050565b6000611229838361120e565b60208301905092915050565b6000602082019050919050565b600061124d826111e2565b61125781856111ed565b9350611262836111fe565b8060005b8381101561129357815161127a888261121d565b975061128583611235565b925050600181019050611266565b5085935050505092915050565b600060208201905081810360008301526112ba8184611242565b905092915050565b60006040820190506112d760008301856110a5565b6112e460208301846110a5565b9392505050565b60008115159050919050565b611300816112eb565b82525050565b600060208201905061131b60008301846112f7565b92915050565b600081519050919050565b600082825260208201905092915050565b6000819050602082019050919050565b6113568161109b565b82525050565b604082016000820151611372600085018261134d565b506020820151611385602085018261134d565b50505050565b6000611397838361135c565b60408301905092915050565b6000602082019050919050565b60006113bb82611321565b6113c5818561132c565b93506113d08361133d565b8060005b838110156114015781516113e8888261138b565b97506113f3836113a3565b9250506001810190506113d4565b5085935050505092915050565b6000602082019050818103600083015261142881846113b0565b905092915050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b6000600282049050600182168061147757607f821691505b60208210810361148a57611489611430565b5b50919050565b7f4e6f74206f776e65720000000000000000000000000000000000000000000000600082015250565b60006114c6600983610f62565b91506114d182611490565b602082019050919050565b600060208201905081810360008301526114f5816114b9565b9050919050565b7f43616d706169676e206e6f7420636f6d706c6574656400000000000000000000600082015250565b6000611532601683610f62565b915061153d826114fc565b602082019050919050565b6000602082019050818103600083015261156181611525565b9050919050565b7f416c726561647920646973747269627574656400000000000000000000000000600082015250565b600061159e601383610f62565b91506115a982611568565b602082019050919050565b600060208201905081810360008301526115cd81611591565b9050919050565b7f4e6f2070726f6669740000000000000000000000000000000000000000000000600082015250565b600061160a600983610f62565b9150611615826115d4565b602082019050919050565b60006020820190508181036000830152611639816115fd565b9050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b60006116a98261109b565b91506116b48361109b565b92508282026116c28161109b565b915082820484148315176116d9576116d861166f565b5b5092915050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601260045260246000fd5b600061171a8261109b565b91506117258361109b565b925082611735576117346116e0565b5b828204905092915050565b7f43616d706169676e20636c6f7365640000000000000000000000000000000000600082015250565b6000611776600f83610f62565b915061178182611740565b602082019050919050565b600060208201905081810360008301526117a581611769565b9050919050565b7f5a65726f20636f6e747269627574696f6e000000000000000000000000000000600082015250565b60006117e2601183610f62565b91506117ed826117ac565b602082019050919050565b60006020820190508181036000830152611811816117d5565b9050919050565b60006118238261109b565b915061182e8361109b565b92508282019050808211156118465761184561166f565b5b92915050565b7f5461726765742065786365656465640000000000000000000000000000000000600082015250565b6000611882600f83610f62565b915061188d8261184c565b602082019050919050565b600060208201905081810360008301526118b181611875565b905091905056fea2646970667358221220206e9b87c996e67c960b584833edea480b261198d938dd70a905f895220dc77064736f6c634300081c0033";

    private static String librariesLinkedBinary;

    public static final String FUNC_COLLECTED = "collected";

    public static final String FUNC_CONTRIBUTE = "contribute";

    public static final String FUNC_CONTRIBUTIONS = "contributions";

    public static final String FUNC_DEADLINE = "deadline";

    public static final String FUNC_DESCRIPTION = "description";

    public static final String FUNC_DISTRIBUTEPROFIT = "distributeProfit";

    public static final String FUNC_DISTRIBUTEDPROFIT = "distributedProfit";

    public static final String FUNC_GETPARTICIPANTS = "getParticipants";

    public static final String FUNC_GETPAYMENTS = "getPayments";

    public static final String FUNC_GETSTATUS = "getStatus";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_PARTICIPANTS = "participants";

    public static final String FUNC_PAYMENTS = "payments";

    public static final String FUNC_PROFITDISTRIBUTED = "profitDistributed";

    public static final String FUNC_STATUS = "status";

    public static final String FUNC_TARGETAMOUNT = "targetAmount";

    @Deprecated
    protected Campaign(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Campaign(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Campaign(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Campaign(String contractAddress, Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<BigInteger> collected() {
        final Function function = new Function(FUNC_COLLECTED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> contribute(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_CONTRIBUTE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<BigInteger> contributions(String param0) {
        final Function function = new Function(FUNC_CONTRIBUTIONS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> deadline() {
        final Function function = new Function(FUNC_DEADLINE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> description() {
        final Function function = new Function(FUNC_DESCRIPTION, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> distributeProfit(BigInteger weiValue) {
        final Function function = new Function(
                FUNC_DISTRIBUTEPROFIT, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<BigInteger> distributedProfit() {
        final Function function = new Function(FUNC_DISTRIBUTEDPROFIT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<List> getParticipants() {
        final Function function = new Function(FUNC_GETPARTICIPANTS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<List> getPayments() {
        final Function function = new Function(FUNC_GETPAYMENTS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Payment>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> getStatus() {
        final Function function = new Function(
                FUNC_GETSTATUS, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> name() {
        final Function function = new Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> participants(BigInteger param0) {
        final Function function = new Function(FUNC_PARTICIPANTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Tuple2<BigInteger, BigInteger>> payments(BigInteger param0) {
        final Function function = new Function(FUNC_PAYMENTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple2<BigInteger, BigInteger>>(function,
                new Callable<Tuple2<BigInteger, BigInteger>>() {
                    @Override
                    public Tuple2<BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<Boolean> profitDistributed() {
        final Function function = new Function(FUNC_PROFITDISTRIBUTED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<BigInteger> status() {
        final Function function = new Function(FUNC_STATUS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> targetAmount() {
        final Function function = new Function(FUNC_TARGETAMOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @Deprecated
    public static Campaign load(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return new Campaign(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Campaign load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Campaign(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Campaign load(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new Campaign(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Campaign load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Campaign(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Campaign> deploy(Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider, String _owner, String _name,
            String _description, BigInteger _targetAmount, BigInteger _deadline) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _owner), 
                new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.Utf8String(_description), 
                new org.web3j.abi.datatypes.generated.Uint256(_targetAmount), 
                new org.web3j.abi.datatypes.generated.Uint256(_deadline)));
        return deployRemoteCall(Campaign.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), encodedConstructor);
    }

    public static RemoteCall<Campaign> deploy(Web3j web3j, TransactionManager transactionManager,
            ContractGasProvider contractGasProvider, String _owner, String _name,
            String _description, BigInteger _targetAmount, BigInteger _deadline) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _owner), 
                new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.Utf8String(_description), 
                new org.web3j.abi.datatypes.generated.Uint256(_targetAmount), 
                new org.web3j.abi.datatypes.generated.Uint256(_deadline)));
        return deployRemoteCall(Campaign.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<Campaign> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit, String _owner, String _name,
            String _description, BigInteger _targetAmount, BigInteger _deadline) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _owner), 
                new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.Utf8String(_description), 
                new org.web3j.abi.datatypes.generated.Uint256(_targetAmount), 
                new org.web3j.abi.datatypes.generated.Uint256(_deadline)));
        return deployRemoteCall(Campaign.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<Campaign> deploy(Web3j web3j, TransactionManager transactionManager,
            BigInteger gasPrice, BigInteger gasLimit, String _owner, String _name,
            String _description, BigInteger _targetAmount, BigInteger _deadline) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _owner), 
                new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.Utf8String(_description), 
                new org.web3j.abi.datatypes.generated.Uint256(_targetAmount), 
                new org.web3j.abi.datatypes.generated.Uint256(_deadline)));
        return deployRemoteCall(Campaign.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), encodedConstructor);
    }

    public static void linkLibraries(List<Contract.LinkReference> references) {
        librariesLinkedBinary = linkBinaryWithReferences(BINARY, references);
    }

    private static String getDeploymentBinary() {
        if (librariesLinkedBinary != null) {
            return librariesLinkedBinary;
        } else {
            return BINARY;
        }
    }

    public static class Payment extends StaticStruct {
        public BigInteger amount;

        public BigInteger timestamp;

        public Payment(BigInteger amount, BigInteger timestamp) {
            super(new org.web3j.abi.datatypes.generated.Uint256(amount), 
                    new org.web3j.abi.datatypes.generated.Uint256(timestamp));
            this.amount = amount;
            this.timestamp = timestamp;
        }

        public Payment(Uint256 amount, Uint256 timestamp) {
            super(amount, timestamp);
            this.amount = amount.getValue();
            this.timestamp = timestamp.getValue();
        }
    }
}
