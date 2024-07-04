package user.management.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import user.management.model.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {
    private final HazelcastInstance hazelcastInstance;

    private IMap<Integer, User> getUserCacheMap(){
        return hazelcastInstance.getMap("userCache");
    }

    public User getFromCache(int id) {
        return getUserCacheMap().get(id);
    }

    public void putIntoCache(int id, User user) {
        getUserCacheMap().put(id, user);
    }

    public void deleteFromCache(int id) {
        getUserCacheMap().delete(id);
    }
}
