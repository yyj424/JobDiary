package ddwucom.mobile.finalproject.ma01_20180985;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class WantedXmlParser {
    private enum TagType { NONE, TOTAL, COMPANY, TITLE, SALTPNM, SAL, REGION, HOLIDAYTPNM,
        MINEDUBG, CAREER, CLOSEDT, INFOURL, ADDR, JOBSCD };

    private final static String FAULT_RESULT = "faultResult";
    private final static String TOTAL_TAG = "total";
    private final static String ITEM_TAG = "wanted";
    private final static String COMPANY_TAG = "company";
    private final static String TITLE_TAG = "title";
    private final static String SALTPNM_TAG = "salTpNm";
    private final static String SAL_TAG = "sal";
    private final static String REGION_TAG = "region";
    private final static String HOLIDAYTPNM_TAG = "holidayTpNm";
    private final static String MINEDUBG_TAG = "minEdubg";
    private final static String CAREER_TAG = "career";
    private final static String CLOSEDT_TAG = "closeDt";
    private final static String INFOURL_TAG = "wantedMobileInfoUrl";
    private final static String ADDR_TAG = "basicAddr";
    private final static String JOBSCD_TAG = "jobsCd";

    private XmlPullParser parser;

    public WantedXmlParser() {
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public List<Wanted> parse1(String xml) {
        List<Wanted> resultList = new ArrayList<>();
        Wanted wtd = null;
        TagType tagType = TagType.NONE;     //  태그를 구분하기 위한 enum 변수 초기화

        try {
            parser.setInput(new StringReader(xml));
            int eventType = parser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();
                        if (tag.equals(ITEM_TAG)) {
                            wtd = new Wanted();
                        }
                        else if (tag.equals(COMPANY_TAG)) {
                            tagType = TagType.COMPANY;
                        }
                        else if (tag.equals(TITLE_TAG)) {
                            tagType = TagType.TITLE;
                        }
                        else if (tag.equals(SALTPNM_TAG)) {
                            tagType = TagType.SALTPNM;
                        }
                        else if (tag.equals(SAL_TAG)) {
                            tagType = TagType.SAL;
                        }
                        else if (tag.equals(REGION_TAG)) {
                            tagType = TagType.REGION;
                        }
                        else if (tag.equals(HOLIDAYTPNM_TAG)) {
                            tagType = TagType.HOLIDAYTPNM;
                        }
                        else if (tag.equals(MINEDUBG_TAG)) {
                            tagType = TagType.MINEDUBG;
                        }
                        else if (tag.equals(CAREER_TAG)) {
                            tagType = TagType.CAREER;
                        }
                        else if (tag.equals(CLOSEDT_TAG)) {
                            tagType = TagType.CLOSEDT;
                        }
                        else if (tag.equals(INFOURL_TAG)) {
                            tagType = TagType.INFOURL;
                        }
                        else if (tag.equals(ADDR_TAG)) {
                            tagType = TagType.ADDR;
                        }
                        else if (tag.equals(JOBSCD_TAG)) {
                            tagType = TagType.JOBSCD;
                        }
                        else if (tag.equals(FAULT_RESULT)) {
                            return null;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(ITEM_TAG)) {
                            resultList.add(wtd);
                        }
                        break;
                    case XmlPullParser.TEXT:
                        String text = parser.getText();
                        switch (tagType) {
                            case COMPANY:
                                wtd.setCompany(text);
                                break;
                            case TITLE:
                                wtd.setTitle(text);
                                break;
                            case SALTPNM:
                                wtd.setSalTpNm(text);
                                break;
                            case SAL:
                                wtd.setSal(text);
                                break;
                            case REGION:
                                wtd.setRegion(text);
                                break;
                            case HOLIDAYTPNM:
                                wtd.setHolidayTpNm(text);
                                break;
                            case MINEDUBG:
                                wtd.setMinEdubg(text);
                                break;
                            case CAREER:
                                wtd.setCareer(text);
                                break;
                            case CLOSEDT:
                                wtd.setCloseDt(text);
                                break;
                            case INFOURL:
                                wtd.setWantedMobileInfoUrl(text);
                                break;
                            case ADDR:
                                wtd.setBasicAddr(text);
                                break;
                            case JOBSCD:
                                wtd.setJobsCd(text);
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }
    public String parse2(String xml) {
        String total = null;
        TagType tagType = TagType.NONE;     //  태그를 구분하기 위한 enum 변수 초기화

        try {
            parser.setInput(new StringReader(xml));
            int eventType = parser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT && total == null) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();
                        if (tag.equals(TOTAL_TAG)) {
                            tagType = TagType.TOTAL;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        switch (tagType) {
                            case TOTAL:
                                total = parser.getText();
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }
}
