package com.lokoko.domain.creator.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("크리에이터 동작")
class CreatorTest {

    @Test
    @DisplayName("SNS 연결 정보는 저장되고 signupCompletedAt 은 최초 한 번만 설정된다")
    void snsLinksAreStoredAndSignupCompletedAtIsStable() {
        Creator creator = Creator.builder().build();

        creator.connectInsta("https://www.instagram.com/lokoko");
        creator.connectTikTok("https://www.tiktok.com/@lokoko");
        creator.updateSignupCompleted();
        var firstCompletedAt = creator.getSignupCompletedAt();
        creator.updateSignupCompleted();

        assertThat(creator.getInstagramUserId()).isEqualTo("https://www.instagram.com/lokoko");
        assertThat(creator.getTikTokUserId()).isEqualTo("https://www.tiktok.com/@lokoko");
        assertThat(firstCompletedAt).isNotNull();
        assertThat(creator.getSignupCompletedAt()).isEqualTo(firstCompletedAt);
    }

    @Test
    @DisplayName("assertHasConnectedSns() : 연결된 SNS 가 없으면 예외를 던진다")
    void assertHasConnectedSns_throwsWhenNoSnsIsConnected() {
        Creator creator = Creator.builder().build();

        assertThatThrownBy(() -> creator.assertHasConnectedSns(IllegalStateException::new))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("assertHasConnectedSns() : 하나 이상 연결되어 있으면 통과한다")
    void assertHasConnectedSns_allowsConnectedCreator() {
        Creator creator = Creator.builder().build();
        creator.connectInsta("lokoko_insta");

        creator.assertHasConnectedSns(IllegalStateException::new);

        assertThat(creator.hasConnectedSns()).isTrue();
    }
}
