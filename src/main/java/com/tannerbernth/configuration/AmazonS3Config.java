package com.tannerbernth.configuration;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3Config {

	@Value("${aws.access.key.id}")
	private String awsKeyId;

	@Value("${aws.access.key.secret}")
	private String awsKeySecret;

	@Value("${aws.region}")
	private String awsRegion;

	@Value("${aws.s3.audio.bucket}")
	private String awsS3AudioBucket;

	@Bean
	public AmazonS3 amazonS3Client() {
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(awsKeyId, awsKeySecret);
		AmazonS3 amazonS3;
		amazonS3 = AmazonS3ClientBuilder.standard()
            		.withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
            		.withRegion(Regions.fromName(awsRegion)).build();
        return amazonS3;
	}

	@Bean
	public String getAWSS3AudioBucket() {
		return awsS3AudioBucket;
	}

	/*//@Bean(name = "awsKeyId")
	public String getAWSKeyId() {
		return awsKeyId;
	}

	//@Bean(name = "awsKeySecret")
	public String getAWSKeySecret() {
		return awsKeySecret;
	}

	//@Bean(name = "awsRegion")
	public Region getAWSPollyRegion() {
		return Region.getRegion(Regions.fromName(awsRegion));
	}

	//@Bean(name = "awsCredentialsProvider")
	public AWSCredentialsProvider getAWSCredentials() {
		BasicAWSCredentials awsCredentials = new BasicAWSCredentials(this.awsKeyId, this.awsKeySecret);
		return new AWSStaticCredentialsProvider(awsCredentials);
	}

	//@Bean(name = "awsS3AudioBucket")
	public String getAWSS3AudioBucket() {
		return awsS3AudioBucket;
	}*/
}