package org.zerock.mreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.zerock.mreview.entity.FreeBoard;

public interface FreeBoardRepository extends JpaRepository<FreeBoard, Long>, QuerydslPredicateExecutor<FreeBoard> {
}

