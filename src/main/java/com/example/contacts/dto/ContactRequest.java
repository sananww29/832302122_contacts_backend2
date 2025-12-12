package com.example.contacts.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class ContactRequest {

	@NotBlank(message = "姓名不能为空")
	@Size(max = 20, message = "姓名不能超过20个字符")
	private String name;

	@Size(max = 10, message = "分类不能超过10个字符")
	private String category;

	@JsonProperty("favorite")
	private boolean isFavorite;

	private List<ContactInfoRequest> contactInfos; // 联系方式列表

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean favorite) {
		isFavorite = favorite;
	}

	public List<ContactInfoRequest> getContactInfos() {
		return contactInfos;
	}

	public void setContactInfos(List<ContactInfoRequest> contactInfos) {
		this.contactInfos = contactInfos;
	}
}


