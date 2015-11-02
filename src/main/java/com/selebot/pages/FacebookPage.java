package com.selebot.pages;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dragos on 9/29/2015.
 */
@Service
public class FacebookPage {
    public static final String URLS_PATH = "/conf/urls.txt";

    public static final String POSTS_PATH = "/conf/posts.txt";


    static Logger LOG = LoggerFactory.getLogger(FacebookPage.class);

    @Resource(name = "webDriver")
    private WebDriver webDriver;

    @Value("${bot.endpoint}")
    private String endpoint;

    @Value("${bot.facebook.username}")
    private String username;

    @Value("${bot.facebook.password}")
    private String password;

    @FindBy(how = How.ID , using="email")
    private WebElement loginUsernameInput;

    @FindBy(how = How.ID , using="pass")
    private WebElement loginPasswordInput;

    private WebElement loginButton;

    private List<String> urls;

    private List<String> posts;

    private static final int MAGIC_NUMBER = 49;

    public void postOnGroups() throws InterruptedException {
        performLogin();

        int i=0,count=0;
        for(String post : posts)
        for (String url : urls){
            i++;
            count++;
            LOG.info("Count {} : Getting url={}",count,url);

            try {
                webDriver.get(url);
                i = doMagicSleep(i);
            }catch (Exception timeout){
                LOG.info("Count {} : Got error while getting url={} . The message was={}",count,url,timeout.getMessage());
                continue;
            }
            webDriver.switchTo().activeElement();
            try {
                writeInPostTextbox(post);
                LOG.info("SUCCESSFULLY posted to url={}",url);
            }catch (Exception e){
                LOG.info("Could not post on group : {}",url);
                try{
                //writeInSellSomething(post);
                }catch(Exception e2){
                    try {
                        clickJoinGroupButton();
                    }catch (Exception ex){
                        LOG.info("Could not click join button for group : {}",url);
                    }
                }
            }
        }
    }

    @PostConstruct
    public void init(){
        loadPosts();
        loadUrls();

        PageFactory.initElements(webDriver, this);
    }

    private void loadUrls() {

        String prop = System.getProperty("config.path");
       if(StringUtils.isNotEmpty(prop)){
           FileReader fileReader = null;
           try {
               fileReader = new FileReader(new File(prop + URLS_PATH));
           } catch (Exception e) {
               e.printStackTrace();
           }
           BufferedReader reader = new BufferedReader(fileReader);
           String line = "";
           urls = new ArrayList<>();
           try {
               while((line= reader.readLine()) != null){
                   urls.add(line);
               }
           } catch (IOException e) {
               e.printStackTrace();
           }

       }else{
           InputStreamReader fileReader = null;
           try {
               fileReader = new InputStreamReader(this.getClass().getResourceAsStream(URLS_PATH));
           } catch (Exception e) {
               e.printStackTrace();
           }
           BufferedReader reader = new BufferedReader(fileReader);
           String line = "";
           urls = new ArrayList<>();
           try {
               while((line= reader.readLine()) != null){
                   urls.add(line);
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
    }

    private void loadPosts(){
        String prop = System.getProperty("config.path");
        if(StringUtils.isNotEmpty(prop)){
            FileReader fileReader = null;
            try {
                fileReader = new FileReader(new File(prop + POSTS_PATH));
            } catch (Exception e) {
                e.printStackTrace();
            }
            BufferedReader reader = new BufferedReader(fileReader);
            String line = "";
            posts = new ArrayList<>();
            try {
                while((line= reader.readLine()) != null){
                    posts.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            InputStreamReader fileReader = null;
            try {
                fileReader = new InputStreamReader(this.getClass().getResourceAsStream(POSTS_PATH));
            } catch (Exception e) {
                e.printStackTrace();
            }
            BufferedReader reader = new BufferedReader(fileReader);
            String line = "";
            posts = new ArrayList<>();
            try {
                while((line= reader.readLine()) != null){
                    posts.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int doMagicSleep(int i) throws InterruptedException {
        if(i==MAGIC_NUMBER){
            LOG.info("Hit count {} .Sleeping for {}",MAGIC_NUMBER,1200);
            TimeUnit.SECONDS.sleep(1200);
            i=0;
        }else{
            LOG.info("Performing regular sleep of {} seconds",10);
            TimeUnit.SECONDS.sleep(10);
        }
        return i;
    }

    public void enterUsername(String username){
        loginUsernameInput.sendKeys(username);
    }

    public void enterPassword(String password){
        loginPasswordInput.sendKeys(password);
    }

    public void clickLogin(){
        loginButton.click();
    }

    public void writeInPostTextbox(String text) throws InterruptedException {
        WebElement postBox = webDriver.findElement(By.cssSelector("textarea[title='Write something...']"));

        postBox.sendKeys(text);
        for(int i=0; i<text.length(); i++) {
            postBox.sendKeys(Keys.BACK_SPACE);
        }
        String ctrlEnter = Keys.chord(Keys.LEFT_CONTROL,Keys.ENTER);
        postBox.sendKeys("#freelings #shareFeelings");
        postBox.sendKeys(ctrlEnter);
        TimeUnit.SECONDS.sleep(3);
    }

    public void writeInSellSomething(String text) throws InterruptedException {
        WebElement postBox = webDriver.findElement(By.cssSelector("input[placeholder='What are you selling?']"));

        TimeUnit.SECONDS.sleep(1);
        postBox.sendKeys("");
        TimeUnit.SECONDS.sleep(1);
        postBox.sendKeys(text);
        TimeUnit.SECONDS.sleep(2);
        WebElement addPrice = webDriver.findElement(By.cssSelector("input[placeholder='Add a price']"));
        TimeUnit.SECONDS.sleep(1);
        new Actions(webDriver).moveToElement(addPrice).perform();
        addPrice.sendKeys("1");
        TimeUnit.SECONDS.sleep(2);


        String ctrlEnter = Keys.chord(Keys.LEFT_CONTROL,Keys.ENTER);
        addPrice.sendKeys(ctrlEnter);
    }


    private void performLogin() {
        LOG.info("Performing login operation for username{} ",username);
        webDriver.get(endpoint);
        enterUsername(username);
        enterPassword(password);
        loginButton = webDriver.findElement(By.cssSelector("input[type='submit'][value='Log In']"));
        clickLogin();
    }

    private void clickJoinGroupButton() {
        WebElement joinButton = webDriver.findElement(By.cssSelector("a[rel='async-post'][role='button']"));
        joinButton.click();
    }

}
