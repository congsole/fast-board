package com.fastcampus.projectboard.service;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.UserAccount;
import com.fastcampus.projectboard.domain.constant.SearchType;
import com.fastcampus.projectboard.dto.ArticleDto;
import com.fastcampus.projectboard.dto.ArticleWithCommentsDto;
import com.fastcampus.projectboard.dto.UserAccountDto;
import com.fastcampus.projectboard.repository.ArticleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스 로직 - 게시글")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {
    @InjectMocks
    private ArticleService sut;
    @Mock
    private ArticleRepository articleRepository;

    @DisplayName("검색어 없이 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test//제목, 본문, id, 닉네임, 해시태그
    void GivenNoSearchParameters_WhenSearchingArticles_ThenReturnsArticleList() {
        // Given
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findAll(pageable)).willReturn(Page.empty());
        // When
        Page<ArticleDto> articles = sut.searchArticles(null, null, pageable);
        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(pageable);
    }
    @DisplayName("검색어와 함께 게시글을 검색하면, 게시글 페이지를 반환한다.")
    @Test
    void givenSearchParameters_whenSearchingArticles_thenReturnsArticlePage() {
        // given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByTitleContaining(searchKeyword, pageable)).willReturn(Page.empty());

        // when
        Page<ArticleDto> articles = sut.searchArticles(searchType, searchKeyword, pageable);

        // then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findByTitleContaining(searchKeyword, pageable);
    }
    @DisplayName("검색어 없이 게시글을 해시태그 검색하면 빈 페이지 반환한다.")
    @Test
    void givenNoSearchParameters_whenSearchingArticlesViaHashtag_thenReturnsEmptyPage() {
        // given
        Pageable pageable = Pageable.ofSize(20);

        // when
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(null, pageable);

        // then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        then(articleRepository).shouldHaveNoInteractions();
    }
    @DisplayName("해시태그 검색하면 게시글 페이지를 반환한다.")
    @Test
    void givenHashtag_whenSearchingArticlesViaHashtag_thenReturnsArticlesPage() {
        // given
        String hashtag = "#java";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByHashtag(hashtag, pageable)).willReturn(Page.empty(pageable));
        // when
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(hashtag, pageable);

        // then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        then(articleRepository).should().findByHashtag(hashtag, pageable);
    }
    @DisplayName("해시태그를 조회하면, 유니크 해시태그 리스트를 반환한다.")
    @Test
    void givenNothing_whenCalling_thenReturnsHashtags() {
        // given
        List<String> expectedHashtags = List.of("#java", "#spring", "#boot");
        given(articleRepository.findAllDistinctHashtags()).willReturn(expectedHashtags);
        // when
        List<String> actualHashtags = sut.getHashtags();
        assertThat(actualHashtags).isEqualTo(expectedHashtags);
        then(articleRepository).should().findAllDistinctHashtags();
    }
//    @DisplayName("게시글을 조회하면, 게시글을 반환한다.")
//    @Test//제목, 본문, id, 닉네임, 해시태그
//    void GivenArticleId_WhenSearchingArticles_ThenReturnsArticle() {
//        // Given
//        Long articleId = 1L;
//        Article article = createArticle();
//        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
//        // When
//        ArticleWithCommentsDto dto = sut.getArticle(articleId);
//        // Then
//        assertThat(dto)
//                .hasFieldOrPropertyWithValue("title", article.getTitle())
//                .hasFieldOrPropertyWithValue("content", article.getContent())
//                .hasFieldOrPropertyWithValue("hashtag", article.getHashtag());
//        then(articleRepository).should().findById(articleId);
//    }

    @DisplayName("없는 게시글을 조회하면, 예외를 던진다.")
    @Test
    void givenNonexistentArticleId_whenSearchingArticle_thenThrowsException() {
        // given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // when
        Throwable t = catchThrowable(() -> sut.getArticle(articleId));

        // then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 없습니다 - articleId: " + articleId);
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글 정보를 입력하면 게시글을 생성한다.")
    @Test
    void givenArticleInfo_whenSavingArticle_thenSavesArticle() {
        // given
        ArticleDto dto = createArticleDto();
        given(articleRepository.save(any(Article.class))).willReturn(createArticle());
        // when
        sut.saveArticle(dto);
        // then
        then(articleRepository).should().save(any(Article.class));
    }



//    @DisplayName("게시글의 수정 정보를 입력하면 게시글을 수정한다.")
//    @Test
//    void givenModifiedArticleInfo_whenUpdatingArticle_thenUpdatesArticle() {
//        // given
//        Article article = createArticle();
//        ArticleDto dto = createArticleDto("새 타이틀", "새 내용", "#springboot");
//        given(articleRepository.getReferenceById(dto.id())).willReturn(article);
//        // when
//        sut.updateArticle(dto);
//        // then
//        assertThat(article)
//                .hasFieldOrPropertyWithValue("title", article.getTitle())
//                .hasFieldOrPropertyWithValue("content", article.getContent())
//                .hasFieldOrPropertyWithValue("hasntag", article.getHashtag());
//        then(articleRepository).should().getReferenceById(dto.id());
//    }
//    @DisplayName("없는 게시글의 수정 정보를 입력하면, 경고 로그를 찍고 아무 것도 하지 않는다.")
//    @Test
//    void givenNonexistingArticleInfo_whenUpdatingArticle_thenLogsWarningAndDoesNothing() {
//        // given
//        ArticleDto dto = createArticleDto("새 타이틀", "새 내용", "#springboot");
//        given(articleRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);
//
//        // when
//        sut.updateArticle(dto);
//
//        // then
//        then(articleRepository).should().getReferenceById(dto.id());
//    }
    @DisplayName("게시글의 ID를 입력하면, 게시글을 삭제한다.")
    @Test
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() {
        // given
        Long articleId = 1L;
        willDoNothing().given(articleRepository).delete(any(Article.class));
        // when
        sut.deleteArticle(articleId);
        // then
        then(articleRepository).should().deleteById(articleId);
    }

    private UserAccount createUserAccount() {
        return UserAccount.of(
                "uno",
                "password",
                "uno@email.com",
                "Uno",
                null
        );
    }

    private Article createArticle() {
        return Article.of(
                createUserAccount(),
                "title",
                "content",
                "#java"
        );
    }

    private ArticleDto createArticleDto() {
        return createArticleDto("title", "content", "#java");
    }

    private ArticleDto createArticleDto(String title, String content, String hashtag) {
        return ArticleDto.of(1L,
                createUserAccountDto(),
                title,
                content,
                hashtag,
                LocalDateTime.now(),
                "Uno",
                LocalDateTime.now(),
                "Uno");
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(

                "uno",
                "password",
                "uno@mail.com",
                "Uno",
                "This is memo",
                LocalDateTime.now(),
                "uno",
                LocalDateTime.now(),
                "uno"
        );
    }
}