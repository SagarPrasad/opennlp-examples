/**
 * 
 */
package com.sagar.solr.domain;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

/**
 * @author A039883
 *
 */
public class Item {
	
	@Field
    String id;
	
	@Field
    String title;
	
	@Field
    String description;
	
	@Field
    Float price;
	
	@Field
    String color;
	
	@Field("cat")
    String[] categories;

    @Field
    List<String> features;

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String[] getCategories() {
		return categories;
	}

	public void setCategories(String[] categories) {
		this.categories = categories;
	}

	public List<String> getFeatures() {
		return features;
	}

	public void setFeatures(List<String> features) {
		this.features = features;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getId() + "--" + this.getCategories() + "--" + this.getFeatures();
	}
	
}
