package ddwucom.mobile.finalproject.ma01_20180985;

import java.util.ArrayList;
import java.util.List;

public class Region {
    private String name;
    private String code;
    private String superCd;
    private List<Region> rList;

    public Region() {
        rList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSuperCd() {
        return superCd;
    }

    public void setSuperCd(String superCd) {
        this.superCd = superCd;
    }

    public List<Region> getrList() {
        return rList;
    }

    public void setrList(List<Region> rList) {
        this.rList.addAll(rList);
    }

    @Override
    public String toString() {
        return name;
    }
}
