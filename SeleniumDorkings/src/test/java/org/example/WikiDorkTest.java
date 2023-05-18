package org.example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

public class WikiDorkTest {
    private static WebDriver driver;

    @BeforeClass
    public static void openBrowser(){
        System.out.print("Opening https://www.google.com/ ...");
        System.setProperty("webdriver.chrome.driver","chromedriver.exe");
        driver = new ChromeDriver();
        driver.navigate().to("https://www.google.com/");
        driver.findElement(By.id("L2AGLb")).click();

        Assert.assertEquals("Step failed.", "Google", driver.getTitle());
        System.out.print("OK\n");
    }

    @Test
    public void searchForKeyword(){
        System.out.print("Searching for the word 'Automatización' ...");
        WebElement searchBar = driver.findElement(By.id("APjFqb"));
        searchBar.sendKeys("Automatización");
        searchBar.submit();

        // Automatización - Buscar con Google
        Assert.assertEquals("Step failed.", "Automatización - Buscar con Google", driver.getTitle());
        System.out.print("OK\n");

        System.out.print("Attempting to click Wikipedia link ...");
        driver.findElement(By.xpath(
                "//*[@id=\"kp-wp-tab-overview\"]/div[1]/div/div/div/" +
                        "div/div/div/div[1]/div/div/div/span[2]/a")).click();

        // Automatización - Wikipedia, la enciclopedia libre
        Assert.assertEquals("Step failed.", "Automatización - Wikipedia, la enciclopedia libre", driver.getTitle());
        System.out.print("OK\n");

        System.out.print("Searching for first automated process in page ...");
        driver.findElement(By.id("toc-La_Revolución_Industrial_en_Europa_Occidental")).click();

        ((JavascriptExecutor) driver).executeScript(
                "Array.from(document.getElementsByTagName('p')).slice(30,35).forEach(x => {" +
                    "Array.from(x.getElementsByTagName('sup')).forEach(sup => sup.remove());" +
                "});");

        ArrayList<String> wikiText = new ArrayList<>(
                driver.findElements(By.tagName("p")).subList(30,35).stream().map(WebElement::getText).toList());

        Pattern regex = Pattern.compile("[0-9]+");
        Matcher matcher;

        ArrayList<String> yearsFromHistory = new ArrayList<>();

        for (String str : wikiText) {
            matcher = regex.matcher(str);

            while (matcher.find()) {
                yearsFromHistory.add(matcher.group());
            }
        }

        System.out.print("OK. " + "Result: Year " + Collections.min(yearsFromHistory) + "\n");

        System.out.print("Saving a screenshot of the wiki page ...");
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(screenshot, new File("WikipediaScreenshot.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.print("OK\t");
    }

    @AfterClass
    public static void closeBrowser(){
        driver.quit();
    }
}