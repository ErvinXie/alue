# 文档

## Unsplash API

Document: https://unsplash.com/documentation

| Access Key | 747fc987bb098bd800270b4f2e684a2fc3414849d9eb3190766266167221d986 |
| :--------: | :----------------------------------------------------------: |
| Secret Key | 309ed53431f826c58d9ad7b280cb051ce3cf23e0d7dee04c54fbce7ba6975a80 |

### Get photos of collection

```url
https://api.unsplash.com/collections/<collection_id>/photos/?client_id=747fc987bb098bd800270b4f2e684a2fc3414849d9eb3190766266167221d986&per_page=30
```



| Name               | Id      |
| ------------------ | ------- |
| alueMainCollection | 4795842 |

JSON returned is like this below: 

```JSON
[
  {
    "id": "LBI7cgq3pbM",
    "created_at": "2016-05-03T11:00:28-04:00",
    "updated_at": "2016-07-10T11:00:01-05:00",
    "width": 5245,
    "height": 3497,
    "color": "#60544D",
    "likes": 12,
    "liked_by_user": false,
    "description": "A man drinking a coffee.",
    "user": {
      "id": "pXhwzz1JtQU",
      "username": "poorkane",
      "name": "Gilbert Kane",
      "portfolio_url": "https://theylooklikeeggsorsomething.com/",
      "bio": "XO",
      "location": "Way out there",
      "total_likes": 5,
      "total_photos": 74,
      "total_collections": 52,
      "instagram_username": "instantgrammer",
      "twitter_username": "crew",
      "profile_image": {
        "small": "https://images.unsplash.com/face-springmorning.jpg?q=80&fm=jpg&crop=faces&fit=crop&h=32&w=32",
        "medium": "https://images.unsplash.com/face-springmorning.jpg?q=80&fm=jpg&crop=faces&fit=crop&h=64&w=64",
        "large": "https://images.unsplash.com/face-springmorning.jpg?q=80&fm=jpg&crop=faces&fit=crop&h=128&w=128"
      },
      "links": {
        "self": "https://api.unsplash.com/users/poorkane",
        "html": "https://unsplash.com/poorkane",
        "photos": "https://api.unsplash.com/users/poorkane/photos",
        "likes": "https://api.unsplash.com/users/poorkane/likes",
        "portfolio": "https://api.unsplash.com/users/poorkane/portfolio"
      }
    },
    "current_user_collections": [ // The *current user's* collections that this photo belongs to.
      {
        "id": 206,
        "title": "Makers: Cat and Ben",
        "published_at": "2016-01-12T18:16:09-05:00",
        "updated_at": "2016-07-10T11:00:01-05:00",
        "curated": false,
        "cover_photo": null,
        "user": null
      },
      // ... more collections
    ],
    "urls": {
      "raw": "https://images.unsplash.com/face-springmorning.jpg",
      "full": "https://images.unsplash.com/face-springmorning.jpg?q=75&fm=jpg",
      "regular": "https://images.unsplash.com/face-springmorning.jpg?q=75&fm=jpg&w=1080&fit=max",
      "small": "https://images.unsplash.com/face-springmorning.jpg?q=75&fm=jpg&w=400&fit=max",
      "thumb": "https://images.unsplash.com/face-springmorning.jpg?q=75&fm=jpg&w=200&fit=max"
    },
    "links": {
      "self": "https://api.unsplash.com/photos/LBI7cgq3pbM",
      "html": "https://unsplash.com/photos/LBI7cgq3pbM",
      "download": "https://unsplash.com/photos/LBI7cgq3pbM/download",
      "download_location": "https://api.unsplash.com/photos/LBI7cgq3pbM/download"
    }
  },
  // ... more photos
]
```



## 数据库设计

| 图片编号（自增主键） | 图片id       | 图片本地缓存路径（NULL） | 图片是否被喜欢(0) | 图片更新时间 |
| -------------------- | ------------ | ------------------------ | ----------------- | ------------ |
| PictureId            | PictureUrlId | Cache                    | Liked             | UpdateTime   |
|                      |              |                          |                   |              |
|                      |              |                          |                   |              |
|                      |              |                          |                   |              |
|                      |              |                          |                   |              |

