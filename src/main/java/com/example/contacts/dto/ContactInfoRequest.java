package com.example.contacts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ContactInfoRequest {
    @NotBlank(message = "联系方式类型不能为空")
    @Size(max = 20, message = "联系方式类型不能超过20个字符")
    private String type;

    @NotBlank(message = "联系方式值不能为空")
    @Size(max = 100, message = "联系方式值不能超过100个字符")
    private String value;

    @JsonProperty("primary")
	private boolean isPrimary;

    // getter 和 setter 方法
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }
}