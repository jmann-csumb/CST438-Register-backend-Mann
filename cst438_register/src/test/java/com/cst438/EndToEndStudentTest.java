package com.cst438;

import com.cst438.domain.*;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class EndToEndStudentTest {

    public static final String CHROME_DRIVER_FILE_LOCATION = "C:/cst438/driver/chromedriver.exe";

    public static final String URL = "http://localhost:3000";

    public static final String TEST_USER_EMAIL = "test@gmail.edu";

    public static final String TEST_USER_NAME = "Test Student";

    public static final String TEST_STATUS = "No Holds";

    public static final int TEST_STATUS_CODE = 0;

    public static final int SLEEP_DURATION = 1000; // 1 second.

 
    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentRepository studentRepository;


    @Test
    public void addStudentTest() throws Exception {
       
       
       Student student = studentRepository.findByEmail(TEST_USER_EMAIL);
       if (student == null) {
           student = new Student();
           student.setEmail(TEST_USER_EMAIL);
           student.setName(TEST_USER_NAME);
           studentRepository.save(student);
       }


                /*
                 * if student is already enrolled, then delete the enrollment.
                 */
                Enrollment x = null;
                do {
                        x = enrollmentRepository.findByEmail(TEST_USER_EMAIL);
                        if (x != null)
                                enrollmentRepository.delete(x);
                } while (x != null);


                /*
                 * if student is already exists, then delete the student.
                 */
                Student studentx = studentRepository.findByEmail(TEST_USER_EMAIL);
        if (studentx != null)
            studentRepository.delete(studentx);


        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        WebDriver driver = new ChromeDriver();
        // Puts an Implicit wait for 10 seconds before throwing exception
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        try {

            driver.get(URL);
            Thread.sleep(SLEEP_DURATION);

            // Locate and click "Add Student" button

            driver.findElement(By.xpath("//a[@href='student/add']")).click();
            Thread.sleep(SLEEP_DURATION);

            // Locate and click "Add Student" button which is the first and only button on the page.
            driver.findElement(By.xpath("//button")).click();
            Thread.sleep(SLEEP_DURATION);

            // enter student name, email, status code = 0 and status note - "No Holds"

            driver.findElement(By.xpath("//input[@name='studentName']")).sendKeys(TEST_USER_NAME);
            driver.findElement(By.xpath("//input[@name='studentEmail']")).sendKeys(TEST_USER_EMAIL);
            WebElement statusCodeElement = driver.findElement(By.xpath("//input[@name='statusCode']"));

            // there is a default value that is set to 0 - clear it
            statusCodeElement.sendKeys(Keys.BACK_SPACE);
            statusCodeElement.sendKeys(Integer.toString(TEST_STATUS_CODE));
            driver.findElement(By.xpath("//input[@name='status']")).sendKeys(TEST_STATUS);
            driver.findElement(By.xpath("//button[@id='Add']")).click();
            Thread.sleep(SLEEP_DURATION);

            Student studentCreated = studentRepository.findByEmail(TEST_USER_EMAIL);

            List<WebElement> elements = driver.findElements(By.xpath("//div[@class='MuiDataGrid-cellContent']"));
            boolean found = false;
            for (WebElement e : elements) {
                System.out.println(e.getText()); // for debug
                if (e.getText().equals(studentCreated.getEmail())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Student created but not visible in the students list");

            // verify that student row has been inserted to database.
            assertNotNull(studentCreated, "Student has not been added to the database");

        } catch (Exception ex) {
            throw ex;
        } finally {

            // clean up database.

            Student studentToDelete = studentRepository.findByEmail(TEST_USER_EMAIL);
            if (studentToDelete != null)
                studentRepository.delete(studentToDelete);
            driver.quit();
        }

    }
}