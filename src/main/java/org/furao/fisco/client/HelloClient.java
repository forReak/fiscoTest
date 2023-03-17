package org.furao.fisco.client;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Properties;
import org.fisco.bcos.sdk.BcosSDK;
import org.fisco.bcos.sdk.abi.datatypes.generated.tuples.generated.Tuple2;
import org.fisco.bcos.sdk.client.Client;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.TransactionReceipt;
import org.furao.fisco.contract.HelloWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class HelloClient {

    static Logger logger = LoggerFactory.getLogger(HelloClient.class);

    private BcosSDK bcosSDK;
    private Client client;
    private CryptoKeyPair cryptoKeyPair;

    String addr = "0xb41e33298f9156cb52f4c4b0160adedd6ad3cb54";


    public void initialize() throws Exception {
        // 函数initialize中进行初始化
        // 初始化BcosSDK
        @SuppressWarnings("resource")
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        bcosSDK = context.getBean(BcosSDK.class);
        // 初始化可向群组1发交易的Client
        client = bcosSDK.getClient(1);
        // 随机生成发送交易的公私钥对
        cryptoKeyPair = client.getCryptoSuite().createKeyPair();
        client.getCryptoSuite().setCryptoKeyPair(cryptoKeyPair);
        logger.debug("create client for group1, account address is " + cryptoKeyPair.getAddress());

    }


    /**
     * 部署合约
     */
    public void deploy(){
        try {
            HelloWorld hello = HelloWorld.deploy(client, cryptoKeyPair);
            System.out.println(
                    " deploy Asset success, contract address is " + hello.getContractAddress());

            recordAddr(hello.getContractAddress());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            System.out.println(" deploy Asset contract failed, error message is  " + e.getMessage());
        }
    }

    /**
     * 存储合约地址
     * @param contractAddress
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void recordAddr(String contractAddress)  throws FileNotFoundException, IOException {
//        Properties prop = new Properties();
//        prop.setProperty("address", contractAddress);
//        final Resource contractResource = new ClassPathResource("contract.properties");
//        FileOutputStream fileOutputStream = new FileOutputStream(contractResource.getFile());
//        prop.store(fileOutputStream, "contract address");
        addr = contractAddress;

    }

    /**
     * 读取合约地址
     * @return
     * @throws Exception
     */
    public String loadAddr() throws Exception {
//        // load Asset contact address from contract.properties
//        Properties prop = new Properties();
//        final Resource contractResource = new ClassPathResource("contract.properties");
//        prop.load(contractResource.getInputStream());
//
//        String contractAddress = prop.getProperty("address");
//        if (contractAddress == null || contractAddress.trim().equals("")) {
//            throw new Exception(" load Asset contract address failed, please deploy it first. ");
//        }
//        logger.info(" load Asset address from contract.properties, address is {}", contractAddress);
        return addr;
    }


    /**
     * 从合约中获取数据
     */
    public void getAmount() {
        try {
            String contractAddress = loadAddr();
            HelloWorld hello = HelloWorld.load(contractAddress, client, cryptoKeyPair);
            String s = hello.get();
            System.out.println(" value is: "+ s);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
            logger.error(" queryAssetAmount exception, error message is {}", e.getMessage());

            System.out.printf(" query asset account failed, error message is %s\n", e.getMessage());
        }
    }

    /**
     * 持久化合约中的属性
     * @param amount
     */
    public void setAccount(String amount) {
        try {
            String contractAddress = loadAddr();

            HelloWorld hello = HelloWorld.load(contractAddress, client, cryptoKeyPair);
            TransactionReceipt receipt = hello.set(amount);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();

            logger.error(" registerAssetAccount exception, error message is {}", e.getMessage());
            System.out.printf(" register asset account failed, error message is %s\n", e.getMessage());
        }
    }


    public static void main(String[] args) throws Exception {
        HelloClient client = new HelloClient();
        client.initialize();
        client.getAmount();;
//        client.setAccount("hello world ! this is a new Value");
    }

}
