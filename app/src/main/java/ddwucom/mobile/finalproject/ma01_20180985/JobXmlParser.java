package ddwucom.mobile.finalproject.ma01_20180985;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class JobXmlParser {
    private enum TagType { NONE, TOTAL, ClCDNM, NAME, CODE, LRCLNM, MDCLNM, SMCLNM, JOBSUM, WAY, SAL, JOBSATIS, JOBPROSPECT,
                            JOBSTATUS, JOBABIL, JOBCHR, JOBINTRST, JOBVALS};

    private final static String FAULT_RESULT = "faultResult";
    private final static String TOTAL_TAG = "total";
    private final static String ClCDNM_TAG = "jobClcdNM";
    private final static String NAME_TAG = "jobNm";
    private final static String NAMES_TAG = "jobsNm";
    private final static String CODE_TAG = "jobCd";
    private final static String CODES_TAG = "jobsCd";
    private final static String LRCLNM_TAG = "jobLrclNm";
    private final static String MDCLNM_TAG = "jobMdclNm";
    private final static String SMCLNM_TAG = "jobSmclNm";
    private final static String JOBSUM_TAG = "jobSum";
    private final static String WAY_TAG = "way";
    private final static String SAL_TAG = "sal";
    private final static String JOBSATIS_TAG = "jobSatis";
    private final static String JOBPROSPECT_TAG = "jobProspect";
    private final static String JOBSTATUS_TAG = "jobStatus";
    private final static String JOBABIL_TAG = "jobAbil";
    private final static String JOBCHR_TAG = "jobChr";
    private final static String JOBINTRST_TAG = "jobIntrst";
    private final static String JOBVALS_TAG = "jobVals";
    private final static String TWODEPTH_TAG = "twoDepth";
    private final static String THREEPTH_TAG = "threeDepth";

    private XmlPullParser parser;

    public JobXmlParser() {
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public List<Job> parse1(String xml) {
        List<Job> resultList = new ArrayList<>();
        TagType tagType = TagType.NONE;     //  태그를 구분하기 위한 enum 변수 초기화
        Job j = null;
        try {
            parser.setInput(new StringReader(xml));
            int eventType = parser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();
                        if (tag.equals(ClCDNM_TAG)) {
                            j = new Job();
                            tagType = TagType.ClCDNM;
                        }
                        else if (tag.equals(NAME_TAG)) {
                            tagType = TagType.NAME;
                        }
                        else if (tag.equals(CODE_TAG)) {
                            tagType = TagType.CODE;
                        }
                        else if (tag.equals(FAULT_RESULT)) {
                            return null;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(NAME_TAG)) {
                            resultList.add(j);
                            j = null;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        String text = parser.getText();
                        switch (tagType) {
                            case ClCDNM:
                                j.setJobClcdNM(text);
                                break;
                            case NAME:
                                j.setJobNm(text);
                                break;
                            case CODE:
                                j.setJobCd(text);
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
    public JobDetail parse3(String xml) {
        JobDetail j = new JobDetail();
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
                        if (tag.equals(LRCLNM_TAG)) {
                            tagType = TagType.LRCLNM;
                        }
                        else if (tag.equals(MDCLNM_TAG)) {
                            tagType = TagType.MDCLNM;
                        }
                        else if (tag.equals(SMCLNM_TAG)) {
                            tagType = TagType.SMCLNM;
                        }
                        else if (tag.equals(JOBSUM_TAG)) {
                            tagType = TagType.JOBSUM;
                        }
                        else if (tag.equals(WAY_TAG)) {
                            tagType = TagType.WAY;
                        }
                        else if (tag.equals(SAL_TAG)) {
                            tagType = TagType.SAL;
                        }
                        else if (tag.equals(JOBSATIS_TAG)) {
                            tagType = TagType.JOBSATIS;
                        }
                        else if (tag.equals(JOBPROSPECT_TAG)) {
                            tagType = TagType.JOBPROSPECT;
                        }
                        else if (tag.equals(JOBSTATUS_TAG)) {
                            tagType = TagType.JOBSTATUS;
                        }
                        else if (tag.equals(JOBABIL_TAG)) {
                            tagType = TagType.JOBABIL;
                        }
                        else if (tag.equals(JOBCHR_TAG)) {
                            tagType = TagType.JOBCHR;
                        }
                        else if (tag.equals(JOBINTRST_TAG)) {
                            tagType = TagType.JOBINTRST;
                        }
                        else if (tag.equals(JOBVALS_TAG)) {
                            tagType = TagType.JOBVALS;
                        }
                        else if (tag.equals(FAULT_RESULT)) {
                            return null;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    case XmlPullParser.TEXT:
                        String text = parser.getText();
                        switch (tagType) {
                            case LRCLNM:
                                j.setJobLrclNm(text);
                                break;
                            case MDCLNM:
                                j.setJobMdclNm(text);
                                break;
                            case SMCLNM:
                                j.setJobSmclNm(text);
                                break;
                            case JOBSUM:
                                j.setJobSum(text);
                                break;
                            case WAY:
                                j.setWay(text);
                                break;
                            case SAL:
                                j.setSal(text);
                                break;
                            case JOBSATIS:
                                j.setJobSatis(text);
                                break;
                            case JOBPROSPECT:
                                j.setJobProspect(text);
                                break;
                            case JOBSTATUS:
                                j.setJobStatus(text);
                                break;
                            case JOBABIL:
                                j.setJobAbil(text);
                                break;
                            case JOBCHR:
                                j.setJobChr(text);
                                break;
                            case JOBINTRST:
                                j.setJobIntrst(text);
                                break;
                            case JOBVALS:
                                j.setJobVals(text);
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
        return j;
    }
    public List<Job> parse4(String xml) {
        List<Job> resultList = new ArrayList<>();
        Job j = null;
        boolean twoDepth = true;
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
                        if (tag.equals(TWODEPTH_TAG)) {
                            j = new Job();
                        }
                        else if (tag.equals(THREEPTH_TAG)) {
                            twoDepth = false;
                        }
                        else if (tag.equals(CODES_TAG) && j != null && twoDepth == true) {
                            tagType = TagType.CODE;
                        }
                        else if (tag.equals(NAMES_TAG) && j != null && twoDepth == true) {
                            tagType = TagType.NAME;
                        }
                        else if (tag.equals(FAULT_RESULT)) {
                            return null;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(TWODEPTH_TAG)) {
                            resultList.add(j);
                            j = null;
                            twoDepth = true;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        String text = parser.getText();
                        switch (tagType) {
                            case CODE:
                                j.setJobCd(text);
                                break;
                            case NAME:
                                j.setJobNm(text);
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
}
