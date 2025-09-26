package com.lokoko.domain.youtube.mapper;

import com.lokoko.domain.media.video.domain.entity.YoutubeVideo;
import com.lokoko.domain.youtube.api.dto.TrendsYoutubeResponse;
import java.util.List;

public interface YoutubeMapper {

    TrendsYoutubeResponse toTrendsResponse(YoutubeVideo video);

    List<TrendsYoutubeResponse> toTrendsResponse(List<YoutubeVideo> videos);
}
