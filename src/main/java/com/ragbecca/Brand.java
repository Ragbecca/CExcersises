package com.ragbecca;

public class Brand {
    private final int brand_id;
    private String company_name;


    public Brand(int brand_id, String company_name) {
        this.brand_id = brand_id;
        this.company_name = company_name;
    }

    public int getBrand_id() {
        return brand_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }
}
