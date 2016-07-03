import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by rishumehrotra on 03/07/16.
 */
public class TagCounter {

    private static Map<String,Integer> tagMap = new HashMap<>();

    @BeforeTest
    public void setChromeDriver() throws Exception
    {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver");
    }

    @Test
    public void tagCounter() throws Exception {

        WebDriver driver = new ChromeDriver();
        driver.get("https://www.stackoverflow.com");

        fireQuery(driver);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        countTagsWithPagination(driver);
        System.out.println(tagMap.size());
        printMaxTag();

    }

    private void printMaxTag() throws Exception {

        Set<Map.Entry<String, Integer>> set = tagMap.entrySet();
        List<Map.Entry<String, Integer>> list = new ArrayList<>(set);
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        for(Map.Entry<String, Integer> entry:list){
            System.out.println(entry.getKey()+" ==== "+entry.getValue());
        }
    }

    private void fireQuery(WebDriver driver) throws Exception {

        driver.findElement(By.name("q")).sendKeys("[qa] created:2016");
        driver.findElement(By.name("q")).sendKeys(Keys.ENTER);

    }

    private void countTagsWithPagination(WebDriver driver) throws Exception {

        //perform 1 mandatory check
        countTagsOnPage(driver);

        //check if pagination is present by checking for next control
        while(isPaginated(driver))
        {
            driver.findElement(By.cssSelector("span[class='page-numbers next']")).click();
            countTagsOnPage(driver);

        }

    }

    private Boolean isPaginated(WebDriver driver) throws Exception {

        if(driver.findElement(By.cssSelector("span[class='page-numbers next']")).isDisplayed())
        {
            return true;
        }
        else
            return false;
    }

    private void countTagsOnPage(WebDriver driver) throws Exception
    {

        //to handle stale element exception
        WebElement myDynamicElement1 = new WebDriverWait(driver, 10).until(
                ExpectedConditions.presenceOfElementLocated(
                        By.className("post-tag")
                )
        );

        List<WebElement> pageElementList = driver.findElements(By.className("post-tag"));

        //now push everything to the map
        for(WebElement element: pageElementList)
        {
            String tagValue = element.getText();
            if(tagMap.containsKey(tagValue))
            {
                //just increment the count
                tagMap.replace(tagValue,tagMap.get(tagValue),tagMap.get(tagValue)+1);
            }
            else
            {
                tagMap.put(tagValue,1);
            }
        }
    }
}
