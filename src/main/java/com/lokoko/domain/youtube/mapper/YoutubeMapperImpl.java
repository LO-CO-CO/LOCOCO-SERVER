package com.lokoko.domain.youtube.mapper;

import com.lokoko.domain.media.video.domain.entity.YoutubeVideo;
import com.lokoko.domain.youtube.api.dto.TrendsYoutubeResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class YoutubeMapperImpl implements YoutubeMapper {

    @Override
    public TrendsYoutubeResponse toTrendsResponse(YoutubeVideo video) {

        return TrendsYoutubeResponse.from(video);
    }

    @Override
    public List<TrendsYoutubeResponse> toTrendsResponse(List<YoutubeVideo> videos) {
        return videos.stream()
                .map(TrendsYoutubeResponse::from)
                .toList();
    }
}
