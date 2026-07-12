package org.example.parnasservice.config;

import java.math.BigInteger;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "parnas.blockchain")
public class BlockchainProperties {

    private boolean enabled = true;
    private String rpcUrl = "http://localhost:8545";
    private long chainId = 31337L;
    private String network = "local";
    private int requiredConfirmations = 1;
    private BigInteger gasLimit = BigInteger.valueOf(3_000_000);
    private String factoryAddress;
    private String deployerPrivateKey;
}
