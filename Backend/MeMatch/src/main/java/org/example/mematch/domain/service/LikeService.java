// org.example.mematch.domain.service.LikeService.java
package org.example.mematch.domain.service;

import org.example.mematch.domain.entities.Like;

public interface LikeService {
    Like likeMeme(Like like);
    boolean hasUserLikedMeme(Long userId, Long memeId);
}
