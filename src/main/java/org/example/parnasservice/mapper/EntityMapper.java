package org.example.parnasservice.mapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import org.example.parnasservice.dto.UserSummary;
import org.example.parnasservice.dto.response.BlockchainTransactionResponse;
import org.example.parnasservice.dto.response.CampaignDetail;
import org.example.parnasservice.dto.response.CampaignFinancials;
import org.example.parnasservice.dto.response.CampaignListItem;
import org.example.parnasservice.dto.response.CampaignSummary;
import org.example.parnasservice.dto.response.ContributionResponse;
import org.example.parnasservice.dto.response.ContributorResponse;
import org.example.parnasservice.dto.response.HomeCampaignCard;
import org.example.parnasservice.dto.response.MyContributionItem;
import org.example.parnasservice.dto.response.PaginationMeta;
import org.example.parnasservice.dto.response.PayoutDistributionResponse;
import org.example.parnasservice.dto.response.PayoutSummary;
import org.example.parnasservice.dto.response.TokenAmount;
import org.example.parnasservice.dto.response.UserProfileResponse;
import org.example.parnasservice.entity.BlockchainTransaction;
import org.example.parnasservice.entity.Campaign;
import org.example.parnasservice.entity.Contribution;
import org.example.parnasservice.entity.Payout;
import org.example.parnasservice.entity.PayoutDistribution;
import org.example.parnasservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface EntityMapper {

    UserSummary toUserSummary(User user);

    UserProfileResponse toUserProfile(User user);

    default TokenAmount toTokenAmount(String raw) {
        return toTokenAmount(raw, 18, "VFT");
    }

    default TokenAmount toTokenAmount(String raw, int decimals, String symbol) {
        if (raw == null) return null;
        TokenAmount ta = new TokenAmount();
        ta.setRaw(raw);
        ta.setDecimals(decimals);
        ta.setSymbol(symbol);
        try {
            BigDecimal rawDecimal = new BigDecimal(new BigInteger(raw), decimals);
            ta.setFormatted(rawDecimal.toPlainString());
        } catch (Exception e) {
            ta.setFormatted(raw);
        }
        return ta;
    }

    default double calcProgressPercent(String raised, String target) {
        try {
            BigInteger r = new BigInteger(raised);
            BigInteger t = new BigInteger(target);
            if (t.compareTo(BigInteger.ZERO) <= 0) return 0;
            return new BigDecimal(r).multiply(BigDecimal.valueOf(100))
                .divide(new BigDecimal(t), 2, java.math.RoundingMode.HALF_UP)
                .doubleValue();
        } catch (Exception e) {
            return 0;
        }
    }

    default PaginationMeta toPaginationMeta(Page<?> page) {
        return new PaginationMeta(
            page.getNumber() + 1,
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }

    @Mapping(target = "shortDescription", expression = "java(truncate(campaign.getDescription(), 180))")
    @Mapping(target = "status", expression = "java(campaign.getStatus().name())")
    @Mapping(target = "targetAmount", expression = "java(toTokenAmount(campaign.getTargetAmount()))")
    @Mapping(target = "raisedAmount", expression = "java(toTokenAmount(campaign.getRaisedAmount()))")
    @Mapping(target = "progressPercent", expression = "java(calcProgressPercent(campaign.getRaisedAmount(), campaign.getTargetAmount()))")
    HomeCampaignCard toHomeCampaignCard(Campaign campaign);

    @Mapping(target = "shortDescription", expression = "java(truncate(campaign.getDescription(), 300))")
    @Mapping(target = "status", expression = "java(campaign.getStatus().name())")
    @Mapping(target = "targetAmount", expression = "java(toTokenAmount(campaign.getTargetAmount()))")
    @Mapping(target = "raisedAmount", expression = "java(toTokenAmount(campaign.getRaisedAmount()))")
    @Mapping(target = "progressPercent", expression = "java(calcProgressPercent(campaign.getRaisedAmount(), campaign.getTargetAmount()))")
    CampaignListItem toCampaignListItem(Campaign campaign);

    default CampaignFinancials toCampaignFinancials(Campaign c, int contributorsCount, int payoutsCount, String totalProfitDistributed) {
        CampaignFinancials f = new CampaignFinancials();
        f.setTargetAmount(toTokenAmount(c.getTargetAmount()));
        f.setRaisedAmount(toTokenAmount(c.getRaisedAmount()));
        f.setRemainingAmount(calcRemaining(c.getTargetAmount(), c.getRaisedAmount()));
        f.setProgressPercent(calcProgressPercent(c.getRaisedAmount(), c.getTargetAmount()));
        f.setContributorsCount(contributorsCount);
        f.setPayoutsCount(payoutsCount);
        f.setTotalProfitDistributed(toTokenAmount(totalProfitDistributed != null ? totalProfitDistributed : "0"));
        return f;
    }

    default CampaignSummary toCampaignSummary(Campaign c, int contributorsCount, int payoutsCount, String totalProfitDistributed) {
        CampaignSummary s = new CampaignSummary();
        s.setId(c.getId());
        s.setContractAddress(c.getContractAddress());
        s.setTitle(c.getTitle());
        s.setShortDescription(truncate(c.getDescription(), 300));
        s.setStatus(c.getStatus().name());
        s.setCreator(toUserSummary(c.getCreator()));
        s.setFinancials(toCampaignFinancials(c, contributorsCount, payoutsCount, totalProfitDistributed));
        s.setCreatedAt(c.getCreatedAt());
        s.setDeadline(c.getDeadline());
        return s;
    }

    default CampaignDetail toCampaignDetail(Campaign c, int contributorsCount, int payoutsCount,
                                             String totalProfitDistributed,
                                             List<PayoutSummary> payouts,
                                             List<BlockchainTransactionResponse> transactions) {
        CampaignDetail d = new CampaignDetail();
        d.setId(c.getId());
        d.setContractAddress(c.getContractAddress());
        d.setTitle(c.getTitle());
        d.setShortDescription(truncate(c.getDescription(), 300));
        d.setDescription(c.getDescription());
        d.setStatus(c.getStatus().name());
        d.setCreator(toUserSummary(c.getCreator()));
        d.setFinancials(toCampaignFinancials(c, contributorsCount, payoutsCount, totalProfitDistributed));
        d.setCreatedAt(c.getCreatedAt());
        d.setDeadline(c.getDeadline());
        d.setBlockchainDataFresh(false);
        d.setBlockchainDataAsOfBlock(null);
        d.setRecentPayouts(payouts);
        d.setRecentTransactions(transactions);
        return d;
    }

    @Mapping(target = "campaignId", source = "campaign.id")
    @Mapping(target = "contributorUserId", source = "contributorUser.id")
    @Mapping(target = "amount", expression = "java(toTokenAmount(contribution.getAmount()))")
    @Mapping(target = "status", expression = "java(contribution.getStatus().name())")
    ContributionResponse toContributionResponse(Contribution contribution);

    default ContributorResponse toContributorResponse(String wallet, String totalContribution,
                                                       double sharePercent, int count) {
        ContributorResponse r = new ContributorResponse();
        r.setWalletAddress(wallet);
        r.setTotalContribution(toTokenAmount(totalContribution));
        r.setSharePercent(sharePercent);
        r.setContributionsCount(count);
        return r;
    }

    @Mapping(target = "campaignId", source = "campaign.id")
    @Mapping(target = "profitAmount", expression = "java(toTokenAmount(payout.getProfitAmount()))")
    @Mapping(target = "status", expression = "java(payout.getStatus().name())")
    @Mapping(target = "recipientsCount", expression = "java(0)")
    PayoutSummary toPayoutSummary(Payout payout);

    @Mapping(target = "payoutId", source = "payout.id")
    @Mapping(target = "contributionAmount", expression = "java(toTokenAmount(distribution.getContributionAmount()))")
    @Mapping(target = "payoutAmount", expression = "java(toTokenAmount(distribution.getPayoutAmount()))")
    @Mapping(target = "transferStatus", expression = "java(distribution.getTransferStatus().name())")
    PayoutDistributionResponse toPayoutDistributionResponse(PayoutDistribution distribution);

    default MyContributionItem toMyContributionItem(Contribution c, int contributorsCount, int payoutsCount, String profitDistributed) {
        MyContributionItem item = new MyContributionItem();
        item.setContribution(toContributionResponse(c));
        item.setCampaign(toCampaignSummary(c.getCampaign(), contributorsCount, payoutsCount, profitDistributed));
        return item;
    }

    @Mapping(target = "value", expression = "java(toTokenAmount(tx.getValue()))")
    @Mapping(target = "type", expression = "java(tx.getType().name())")
    @Mapping(target = "status", expression = "java(tx.getStatus().name())")
    BlockchainTransactionResponse toBlockchainTransactionResponse(BlockchainTransaction tx);

    default TokenAmount calcRemaining(String target, String raised) {
        try {
            BigInteger t = new BigInteger(target);
            BigInteger r = new BigInteger(raised);
            return toTokenAmount(t.subtract(r).toString());
        } catch (Exception e) {
            return toTokenAmount("0");
        }
    }

    default String truncate(String s, int maxLen) {
        if (s == null) return null;
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen);
    }
}
