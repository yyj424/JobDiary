package ddwucom.mobile.finalproject.ma01_20180985;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class RegionXmlParser {
    private enum TagType { NONE, SUPERNM, SUBNM, SUPERCD, SUBCD };

    private final static String FAULT_RESULT = "faultResult";
    private final static String ITEM_TAG1 = "oneDepth";
    private final static String ITEM_TAG2 = "twoDepth";
    private final static String NAME_TAG = "regionNm";
    private final static String CODE_TAG = "regionCd";
    private final static String SUPERCD_TAG = "superCd";

    private XmlPullParser parser;

    public RegionXmlParser() {
        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public List<Region> parse1(String xml) {
        List<Region> resultList = new ArrayList<>();
        List<Region> subRList = new ArrayList<>();
        Region sup = null;
        Region sub = null;
        RegionXmlParser.TagType tagType = RegionXmlParser.TagType.NONE;     //  태그를 구분하기 위한 enum 변수 초기화

        try {
            parser.setInput(new StringReader(xml));
            int eventType = parser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();
                        if (tag.equals(ITEM_TAG1)) {
                            sup = new Region();
                        }
                        else if (tag.equals(ITEM_TAG2)) {
                            sub = new Region();
                        }
                        else if (tag.equals(CODE_TAG)) {
                            if (sup != null && sub == null) {
                                tagType = TagType.SUPERCD;
                            }
                            else if (sup != null && sub != null) {
                                tagType = TagType.SUBCD;
                            }
                        }
                        else if (tag.equals(NAME_TAG)) {
                            if (sup != null && sub == null) {
                                tagType = TagType.SUPERNM;
                            }
                            else if (sup != null && sub != null) {
                                tagType = TagType.SUBNM;
                            }
                        }
                        else if (tag.equals(FAULT_RESULT)) {
                            return null;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(ITEM_TAG1)) {
                            resultList.add(sup);
                            subRList.clear();
                            sup = null;
                        }
                        else if (parser.getName().equals(ITEM_TAG2)) {
                            sup.getrList().add(sub);
                            sub = null;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        String text = parser.getText();
                        switch (tagType) {
                            case SUPERCD:
                                sup.setSuperCd(text);
                                break;
                            case SUPERNM:
                                sup.setName(text);
                                break;
                            case SUBCD:
                                sub.setCode(text);
                                break;
                            case SUBNM:
                                sub.setName(text);
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
