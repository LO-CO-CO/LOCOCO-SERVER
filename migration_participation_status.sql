-- ParticipationStatus 마이그레이션 SQL
-- Deprecated 상태들을 새로운 6가지 상태로 변경

-- ========================================
-- 1. 마이그레이션 전 현재 상태 확인
-- ========================================
SELECT 'Before Migration' as phase, status, COUNT(*) as count
FROM creator_campaigns
GROUP BY status
ORDER BY count DESC;

-- ========================================
-- 2. 기존 데이터 백업 (선택사항 - 운영환경에서는 권장)
-- ========================================
-- CREATE TABLE creator_campaigns_backup AS
-- SELECT * FROM creator_campaigns WHERE status IN (
--     'APPROVED_ADDRESS_CONFIRMED',
--     'APPROVED_FIRST_REVIEW_DONE',
--     'APPROVED_REVISION_REQUESTED',
--     'APPROVED_REVISION_CONFIRMED',
--     'APPROVED_SECOND_REVIEW_DONE',
--     'APPROVED_ADDRESS_NOT_CONFIRMED',
--     'APPROVED_REVIEW_NOT_CONFIRMED'
-- );

-- ========================================
-- 3. DEPRECATED 상태들을 새로운 상태로 마이그레이션
-- ========================================

-- 3-1. ACTIVE 상태로 변경 (진행 중인 상태들)
UPDATE creator_campaigns
SET status = 'ACTIVE'
WHERE status IN (
    'APPROVED_ADDRESS_CONFIRMED',      -- 주소 확정 후 리뷰 대기
    'APPROVED_FIRST_REVIEW_DONE',      -- 1차 리뷰 완료 후 2차 대기
    'APPROVED_REVISION_REQUESTED',     -- 수정 요청된 상태
    'APPROVED_REVISION_CONFIRMED'      -- 수정사항 확인 후 재업로드 대기
);

-- 3-2. COMPLETED 상태로 변경 (완료된 상태들)
UPDATE creator_campaigns
SET status = 'COMPLETED'
WHERE status IN (
    'APPROVED_SECOND_REVIEW_DONE'      -- 2차 리뷰 완료 (진짜 완료!)
);

-- 3-3. EXPIRED 상태로 변경 (만료된 상태들)
UPDATE creator_campaigns
SET status = 'EXPIRED'
WHERE status IN (
    'APPROVED_ADDRESS_NOT_CONFIRMED',  -- 주소 미확정으로 만료
    'APPROVED_REVIEW_NOT_CONFIRMED'    -- 리뷰 미제출로 만료
);

-- ========================================
-- 4. 마이그레이션 결과 확인
-- ========================================
SELECT 'After Migration' as phase, status, COUNT(*) as count
FROM creator_campaigns
GROUP BY status
ORDER BY count DESC;

-- ========================================
-- 5. 검증: 새로운 6가지 상태만 있는지 확인
-- ========================================
-- 예상 결과: PENDING, APPROVED, ACTIVE, COMPLETED, EXPIRED, REJECTED 만 있어야 함
SELECT
    CASE
        WHEN status IN ('PENDING', 'APPROVED', 'ACTIVE', 'COMPLETED', 'EXPIRED', 'REJECTED')
        THEN 'VALID'
        ELSE 'INVALID'
    END as validation_status,
    status,
    COUNT(*) as count
FROM creator_campaigns
GROUP BY status
ORDER BY validation_status, count DESC;

-- ========================================
-- 6. 마이그레이션 완료 확인 쿼리
-- ========================================
-- 이 쿼리 결과가 0이어야 마이그레이션 완료
SELECT COUNT(*) as remaining_deprecated_statuses
FROM creator_campaigns
WHERE status IN (
    'APPROVED_ADDRESS_CONFIRMED',
    'APPROVED_FIRST_REVIEW_DONE',
    'APPROVED_REVISION_REQUESTED',
    'APPROVED_REVISION_CONFIRMED',
    'APPROVED_SECOND_REVIEW_DONE',
    'APPROVED_ADDRESS_NOT_CONFIRMED',
    'APPROVED_REVIEW_NOT_CONFIRMED'
);

-- ========================================
-- 7. 다른 테이블들도 확인 (필요시)
-- ========================================
-- 혹시 다른 테이블에서도 ParticipationStatus를 저장한다면:

-- 로그 테이블 예시:
-- UPDATE creator_campaign_logs
-- SET old_status = 'ACTIVE' WHERE old_status = 'APPROVED_ADDRESS_CONFIRMED';
-- UPDATE creator_campaign_logs
-- SET new_status = 'ACTIVE' WHERE new_status = 'APPROVED_ADDRESS_CONFIRMED';

-- 기타 테이블 예시:
-- UPDATE notifications SET related_status = 'ACTIVE' WHERE related_status = 'APPROVED_ADDRESS_CONFIRMED';