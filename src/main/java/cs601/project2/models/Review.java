package cs601.project2.models;

import java.util.Date;

public class Review {
	private String id;
	private String reviewerId;
	private String asin;
	private String reviewerName;
	private String reviewText;
	private double overall;
	private String summary;
	private int unixReviewTime;
	private Date reviewTime;
	
	public Review(String id, String reviewerId, String asin, String reviewerName, String reviewText, double overall,
			String summary, int unixReviewTime, Date reviewTime) {
		super();
		this.id = id;
		this.reviewerId = reviewerId;
		this.asin = asin;
		this.reviewerName = reviewerName;
		this.reviewText = reviewText;
		this.overall = overall;
		this.summary = summary;
		this.unixReviewTime = unixReviewTime;
		this.reviewTime = reviewTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReviewerId() {
		return reviewerId;
	}

	public void setReviewerId(String reviewerId) {
		this.reviewerId = reviewerId;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public String getReviewerName() {
		return reviewerName;
	}

	public void setReviewerName(String reviewerName) {
		this.reviewerName = reviewerName;
	}

	public String getReviewText() {
		return reviewText;
	}

	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}

	public double getOverall() {
		return overall;
	}

	public void setOverall(double overall) {
		this.overall = overall;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public int getUnixReviewTime() {
		return unixReviewTime;
	}

	public void setUnixReviewTime(int unixReviewTime) {
		this.unixReviewTime = unixReviewTime;
	}

	public Date getReviewTime() {
		return reviewTime;
	}

	public void setReviewTime(Date reviewTime) {
		this.reviewTime = reviewTime;
	}
	
}
