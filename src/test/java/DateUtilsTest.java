
import com.perfectchina.bns.WebApplication;
import com.perfectchina.bns.common.utils.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * @author: lightway
 * @describe:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = WebApplication.class,properties = {"spring.profiles.active=prod"})
public class DateUtilsTest {

	@Test
	public void testGetLastMonthSnapshotDate(){
		String snapshotDate= "201801";
		String lastMonthSnapshotDate = DateUtils.getLastMonthSnapshotDate(snapshotDate);
		System.out.println(lastMonthSnapshotDate);
		String lastMonthSnapshotDate2 = DateUtils.getLastMonthSnapshotDate();
		System.out.println(lastMonthSnapshotDate2);
		System.out.print("==============================================");
	}

	@Test
	public void testGetPreviousDateEndTime(){
		Date currentDate = new Date();
		Date previousDateEndTime = DateUtils.getPreviousDateEndTime( currentDate );
		System.out.print("==============================================");
		System.out.print(previousDateEndTime);
	}

	@Test
	public void testGetLastMonthStartDate(){
		Date currentDate = new Date();
		Date previousDateEndTime = DateUtils.getLastMonthStartDate( currentDate );
		System.out.print("==============================================");
		System.out.print(previousDateEndTime);
	}

}
