//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.perfectchina.bns.WebApplication;
import com.perfectchina.bns.common.utils.DateUtils;

/**
* @author: lightway
* @describe:
*/
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes=WebApplication.class)
public class DateUtilsTest {
	
	//@Test
	void testGetLastMonthSnapshotDate(){
		String snapshotDate= "201801";
		String lastMonthSnapshotDate = DateUtils.getLastMonthSnapshotDate(snapshotDate);
		System.out.println(lastMonthSnapshotDate);
	}

}
