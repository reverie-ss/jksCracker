package com.reverieworks.jksCracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.ByteArrayResource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import java.io.*;
import java.security.*;
import org.springframework.core.io.Resource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/jks")
public class JksCrackerController {
    private List<BucketList> myBucketList = new ArrayList();
    private final AtomicLong counter = new AtomicLong();
    
    private final static JKS jks = new JKS();
  
    @CrossOrigin
    @PostMapping("/uploadFile")
    public byte[] uploadFile(@RequestParam("file") MultipartFile file, @RequestParam(value="password") String password) throws Exception {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        InputStream inputStream =  new BufferedInputStream(file.getInputStream());
        String passwd = "xyz";
        jks.engineLoad(inputStream, passwd.toCharArray());
        inputStream.close();

        System.out.printf("Changing password on, writing to...\n");

        OutputStream out = new FileOutputStream(file.getOriginalFilename() + "2");
        jks.engineStore(out, passwd.toCharArray());
        ByteArrayOutputStream outputStream = jks.getByteArrayOutputStream();
        out.close();

        System.out.printf(outputStream.size() + " ");
        return outputStream.toByteArray();
    }

    @GetMapping(value = "/")
    public ResponseEntity index() {
        return ResponseEntity.ok(myBucketList);
    }
    @GetMapping(value = "/bucket")
    public ResponseEntity getBucket(@RequestParam(value="id") Long id) {
        BucketList itemToReturn = null;
        for(BucketList bucket : myBucketList){
            if(bucket.getId() == id)
                itemToReturn = bucket;
        }
        return ResponseEntity.ok(itemToReturn);
    }
    @PostMapping(value = "/")
    public ResponseEntity addToBucketList(@RequestParam(value="name") String name) {
        myBucketList.add(new BucketList(counter.incrementAndGet(), name));
        return ResponseEntity.ok(myBucketList);
    }
    @PutMapping(value = "/")
    public ResponseEntity updateBucketList(@RequestParam(value="name") String name, @RequestParam(value="id") Long id) {
        myBucketList.forEach(bucketList ->  {
            if(bucketList.getId() == id){
                bucketList.setName(name);
            }
        });
        return ResponseEntity.ok(myBucketList);
    }
    @DeleteMapping(value = "/")
    public ResponseEntity removeBucketList(@RequestParam(value="id") Long id) {
        BucketList itemToRemove = null;
        for(BucketList bucket : myBucketList){
            if(bucket.getId() == id)
                itemToRemove = bucket;
        }
        myBucketList.remove(itemToRemove);
        return ResponseEntity.ok(myBucketList);
    }

}
