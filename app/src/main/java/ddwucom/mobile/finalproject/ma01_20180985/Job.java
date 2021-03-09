package ddwucom.mobile.finalproject.ma01_20180985;

public class Job {
    private String jobClcdNM;
    private String jobNm;
    private String jobCd;

    public Job() {
    }

    public String getJobClcdNM() {
        return jobClcdNM;
    }

    public void setJobClcdNM(String jobClcdNM) {
        this.jobClcdNM = jobClcdNM;
    }

    public String getJobNm() {
        return jobNm;
    }

    public void setJobNm(String jobNm) {
        this.jobNm = jobNm;
    }

    public String getJobCd() {
        return jobCd;
    }

    public void setJobCd(String jobCd) {
        this.jobCd = jobCd;
    }

    @Override
    public String toString() {
        return jobNm;
    }
}
