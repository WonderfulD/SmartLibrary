package com.ruoyi.Utils;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyuncs.exceptions.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.UUID;

@Component
@Slf4j
public class AliyunOSSUtils {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Value("${aliyun.oss.region}")
    private String region;

    @Value("${aliyun.oss.url}")
    private String baseUrl;

    private OSS ossClient;

    /**
     * 初始化 OSSClient 实例
     */
    @PostConstruct
    public void init() throws ClientException {
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        this.ossClient = OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();
    }

    /**
     * 上传文件到阿里云 OSS
     *
     * @param file MultipartFile 文件对象
     * @return 上传后的文件 URL
     * @throws Exception 上传过程中可能抛出的异常
     */
    public String uploadFile(MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件为空！");
        }

        // 生成唯一的文件名，避免覆盖
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String objectName = UUID.randomUUID().toString().replace("-", "") + fileExtension;

        try {
            InputStream inputStream = file.getInputStream();
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            // 返回文件的完整 URL
            return baseUrl + "/" + objectName;
        } catch (Exception e) {
            log.info("上传文件错误：{}", e.getMessage());
            throw new Exception("上传文件到阿里云 OSS 失败", e);
        }
    }

    /**
     * 关闭 OSSClient
     */
    public void shutdown() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
}
