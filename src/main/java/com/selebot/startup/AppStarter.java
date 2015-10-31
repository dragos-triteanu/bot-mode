package com.selebot.startup;

import com.selebot.config.AppConfig;
import com.selebot.pages.FacebookPage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

/**
 * Created by Dragos on 9/29/2015.
 */
public class AppStarter {


    public static void main(String args[]) throws InterruptedException, IOException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);

        FacebookPage page = ctx.getBean("facebookPage",FacebookPage.class);
        page.postOnGroups();
    }


}
