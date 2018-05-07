package cn.guanxiaoda.spider;

import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author guanxiaoda
 * @date 2018/5/3
 */
public class LogAnalyseTest {

    @Test
    public void test() throws IOException {
        Pattern insertSuccess = Pattern.compile("Inserted poi_id.*SUCCESS");
        Pattern updateSuccess = Pattern.compile("Updated poi_id.*SUCCESS");
        Pattern updateFailure = Pattern.compile("Updated poi_id.*ERROR");
        Pattern updateNotChange = Pattern.compile("Updated poi_id.*Nothing Is Changed");
        Pattern poiIdRaw = Pattern.compile("poi_id: \\d+ into");
        AtomicInteger insertSuccessCount = new AtomicInteger();
        AtomicInteger updateSuccessCount = new AtomicInteger();
        AtomicInteger updateFailureCount = new AtomicInteger();
        AtomicInteger updateNotChangeCount = new AtomicInteger();
        AtomicInteger otherCount = new AtomicInteger();
        AtomicInteger lineNum = new AtomicInteger();

        Set<String> insertSuccessPois = new HashSet<>();
        Set<String> updateSuccessPois = new HashSet<>();
        Set<String> updateFailurePois = new HashSet<>();
        Set<String> updateNotChangePois = new HashSet<>();

        Resources.asCharSource(Resources.getResource("logs/TravelPoi_Qunar_Wap_Parser.log.2018-05-02"), Charset.defaultCharset()).readLines(new LineProcessor<String>() {
            @Override
            public boolean processLine(String line) throws IOException {
                System.out.println("processing: " + lineNum.getAndIncrement());
//                String poiId = "poi_id: {0} into"
                Matcher m = poiIdRaw.matcher(line);
                String poiId = null;
                if (m.find()) {
                    poiId = m.group().replace("poi_id: ", "").replace(" into", "").trim();
                }
                if (insertSuccess.matcher(line).find()) {
                    insertSuccessCount.getAndIncrement();
                    insertSuccessPois.add(poiId);
                } else if (updateSuccess.matcher(line).find()) {
                    updateSuccessCount.incrementAndGet();
                    updateSuccessPois.add(poiId);
                } else if (updateFailure.matcher(line).find()) {
                    updateFailureCount.incrementAndGet();
                    updateFailurePois.add(poiId);
                } else if (updateNotChange.matcher(line).find()) {
                    updateNotChangeCount.incrementAndGet();
                    updateNotChangePois.add(poiId);
                } else {
                    otherCount.getAndIncrement();
                }
                return true;
            }

            @Override
            public String getResult() {
                System.out.println("insertSuccessCount:" + insertSuccessCount);
                System.out.println("updateSuccessCount:" + updateSuccessCount);
                System.out.println("updateFailureCount:" + updateFailureCount);
                System.out.println("updateNotChangeCount:" + updateNotChangeCount);
                System.out.println("otherCount:" + otherCount);
                return null;
            }
        });

        System.out.println("insertSuccessCount:" + insertSuccessCount);
        System.out.println("updateSuccessCount:" + updateSuccessCount);
        System.out.println("updateFailureCount:" + updateFailureCount);
        System.out.println("updateNotChangeCount:" + updateNotChangeCount);
        System.out.println("otherCount:" + otherCount);
        System.out.println("totalLines:" + lineNum);


        System.out.println("*******distinct");
        System.out.println("insertSuccessCount:" + insertSuccessPois.size());
        System.out.println("updateSuccessCount:" + updateSuccessPois.size());
        System.out.println("updateFailureCount:" + updateFailurePois.size());
        System.out.println("updateNotChangeCount:" + updateNotChangePois.size());

        Set<String> all = new HashSet<>();
        all.addAll(insertSuccessPois);
        all.addAll(updateSuccessPois);
        all.addAll(updateFailurePois);
        all.addAll(updateNotChangePois);
        System.out.println("all:" + all.size());

    }
}
