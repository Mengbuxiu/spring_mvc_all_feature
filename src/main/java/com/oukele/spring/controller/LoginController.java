package com.oukele.spring.controller;

import com.oukele.spring.entity.Person;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController {
    @RequestMapping("/login")
    public String login(@RequestHeader(value = "Cache-Control", required = false) String header) {
        return "index";
    }

    @GetMapping("/person")
    public ResponseEntity<Person> showBook() {
        Person person = new Person("精神小伙", 100);
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                .eTag("1.1.1") // lastModified is also available
                .body(person);
    }

    @GetMapping("/cache/etag")
    public ResponseEntity<String> etag(
            //浏览器验证文档内容的实体 If-None-Match
            @RequestHeader (value = "If-None-Match", required = false) String ifNoneMatch) {

        // 当前系统时间
        long now = System.currentTimeMillis();
        // 文档可以在浏览器端/proxy上缓存多久(second)
        long maxAge = 10;

        String body = "this is a weak ETag";
        // https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#filters-shallow-etag
        String etag = "W/\"" + DigestUtils.md5DigestAsHex(body.getBytes()) + "\"";

        if(etag.equals(ifNoneMatch)) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
        // 模拟业务处理
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DateFormat gmtDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        MultiValueMap<String, String> headers = new HttpHeaders();

        //ETag http 1.1支持
        headers.add("ETag", etag);
        //当前系统时间
        headers.add("Date", gmtDateFormat.format(new Date(now)));
        //文档生存时间 http 1.1支持
        headers.add("Cache-Control", "max-age=" + maxAge);
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    @GetMapping("/cache/expire")
    public ResponseEntity<String> expire(//为了方便测试，此处传入文档最后修改时间
                                         @RequestParam("millis") long lastModifiedMillis,
                                         //浏览器验证文档内容是否修改时传入的Last-Modified
                                         @RequestHeader(value = "If-Modified-Since", required = false) Date ifModifiedSince) {

        //当前系统时间
        long now = System.currentTimeMillis();
        //文档可以在浏览器端/proxy上缓存多久
        long maxAge = 20;

        //判断内容是否修改了，此处使用等值判断
        if(ifModifiedSince != null && ifModifiedSince.getTime() == lastModifiedMillis) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }

        DateFormat gmtDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);

        String body = "this is a weak ETag";
        MultiValueMap<String, String> headers = new HttpHeaders();

        //文档修改时间
        headers.add("Last-Modified", gmtDateFormat.format(new Date(lastModifiedMillis)));

        //当前系统时间
        headers.add("Date", gmtDateFormat.format(new Date(now)));
        //过期时间 http 1.0支持
        headers.add("Expires", gmtDateFormat.format(new Date(now + maxAge)));
        //文档生存时间 http 1.1支持
        headers.add("Cache-Control", "max-age=" + maxAge);
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }
}
