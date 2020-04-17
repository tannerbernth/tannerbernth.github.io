package com.tannerbernth.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.tannerbernth.web.error.StorageException;

@Service
public class AmazonS3Service {

    @Autowired
    private String awsS3AudioBucket;

    @Autowired
    private AmazonS3 amazonS3;

    /*@Autowired
    public AmazonS3Service(Region awsRegion, AWSCredentialsProvider awsCredentialsProvider, String awsS3AudioBucket) {
        this.amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(awsCredentialsProvider)
                .withRegion(awsRegion.getName()).build();
        this.awsS3AudioBucket = awsS3AudioBucket;
    }*/

    public AmazonS3Service() {
        super();
    }

    @Async
    public void uploadFileToS3Bucket(MultipartFile multipartFile, String filename, boolean enablePublicReadAccess) {
        //String fileName = multipartFile.getOriginalFilename();

        try {
            //creating the file in the server (temporarily)
            File file = new File(filename);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(multipartFile.getBytes());
            fos.close();

            PutObjectRequest putObjectRequest = new PutObjectRequest(awsS3AudioBucket, filename, file);

            if (enablePublicReadAccess) {
                putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
            }
            this.amazonS3.putObject(putObjectRequest);
            //removing the file created in the server
            file.delete();
        } catch (IOException | AmazonServiceException ex) {
            //logger.error("error [" + ex.getMessage() + "] occurred while uploading [" + fileName + "] ");
            throw new StorageException("Failed to store file " + filename);
        }
    }

    @Async
    public void deleteFileFromS3Bucket(String filename) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(awsS3AudioBucket, filename));
        } catch (AmazonServiceException ex) {
            //logger.error("error [" + ex.getMessage() + "] occurred while removing [" + fileName + "] ");
            throw new StorageException("Failed to store file " + filename);
        }
    }
}