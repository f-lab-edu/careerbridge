package com.careerbridge.match.service;

import com.careerbridge.match.domain.Match;
import com.careerbridge.match.repository.MatchRepository;
import com.careerbridge.mentor.entity.Mentor;
import com.careerbridge.mentor.repository.MentorRepository;
import com.careerbridge.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;

    private final ProductRepository productRepository;

    public List<Match> SearchMatchByMentee(Long productId, Long menteeId){
        checkProductId(productId);
        List<Match> matches = matchRepository.findByProductIdAndMenteeId(productId,menteeId);
        return matches;
    }

    public List<Match> SearchMatchByMentor(Long productId, Long mentorId){
        checkProductId(productId);
        List<Match> matches = matchRepository.findByProductIdAndProductMentorId(productId,mentorId);
        return matches;
    }
    private void checkProductId(Long productId){
        if(productRepository.findById(productId).isEmpty()){
            throw new IllegalArgumentException("존재하지 않는 상품 코드입니다.");
        }
    }
}
