
import com.perfectchina.bns.WebApplication;
import com.perfectchina.bns.common.utils.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

	@Test
	public void testGetCurrentStartEndTime(){
		Date currentDateStartTime = DateUtils.getCurrentDateStartTime(new Date());
		Date currentDateEndTime = DateUtils.getCurrentDateEndTime(new Date());
		System.out.print("==============================================");
		System.out.print(currentDateStartTime);
		System.out.print(currentDateEndTime);
	}

	@Test
	public void testGetCurrentMonthStartEndTime(){
		Date date= null;
		try {
			date = new SimpleDateFormat("yyyyMM").parse("201809");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date currentDateStartTime = DateUtils.getCurrentMonthStartDate(date);
		Date currentDateEndTime = DateUtils.getCurrentMonthEndDate(date);
		System.out.print("==============================================");
		System.out.print(currentDateStartTime);
		System.out.print(currentDateEndTime);
	}

	@Test
	public void test(){
		System.out.println(ManthUtils.round(1.005F));
		System.out.println(ManthUtils.round(1.095F));
		System.out.println(ManthUtils.round(1.004F));
		System.out.println(ManthUtils.round(1.04F));
		System.out.println(ManthUtils.round(1.05F));
	}

}
