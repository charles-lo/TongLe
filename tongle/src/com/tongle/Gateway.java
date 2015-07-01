package com.tongle;

import java.util.ArrayList;
import java.util.List;

public interface Gateway {

	void getMainPageData(MainPageDataListener listener, String AdminArea);
	
	void getTopic(TopicListener listener, String id);
	
	void getActivity(ActivityListener listener, String id);

	
	//
	public interface MainPageDataListener {

		void onComplete(MainPageData data);

		void onError();
	}
	
	public interface TopicListener {

		void onComplete(TopicData data);

		void onError();
	}
	
	public interface ActivityListener {

		void onComplete(ActivityData data);

		void onError();
	}

	class MainPageData {
		List<Headline> mHeadlines = new ArrayList<Headline>();
		List<Topic> mTopList = new ArrayList<Topic>();
	}

	class Headline {
		String mActivityID;
		String mPicture;
		String mTitle;
		String mArea;

		Headline(String id, String picture, String Title, String Area) {
			mActivityID = id;
			mPicture = picture;
			mTitle = Title;
			mArea = Area;
		}
	}

	class Topic {
		String mID;
		String mPicture;
		String mTitle;

		Topic(String id, String picture, String Title) {
			mID = id;
			mPicture = picture;
			mTitle = Title;
		}
	}

	class TopicData {
		String mID;
		String mPicture;
		String mTitle;
		List<Content> mContents = new ArrayList<Content>();
	}

	class Content {
		int mType;
		String mText;
		String mPicture;
		String mActivityID;

		Content(int type, String text, String picture, String activityID) {
			mType = type;
			mText = text;
			mPicture = picture;
			mActivityID = activityID;
		}
	}
	
	class ActivityData {
		String mID;
		String mPicture;
		String mTitle;
		String mBeginDate;
		String mEndDate;
		String mPlace;
		String mAddress;
		String mOrganizer;
		String mDescription;
		int mPhysical;
		int mAesthetic;
		int mScience;
		int mSocially;
		int mCulture;
		List<ActivityAgeLevelSetting> mActivityAgeLevelSettings = new ArrayList<ActivityAgeLevelSetting>();
	}
	
	class ActivityAgeLevelSetting {
		String mID;
		String mDescription;

		ActivityAgeLevelSetting(String ID, String description) {
			mID = ID;
			mDescription = description;
		}
	}
}