package raio.chat.application.port;

import raio.chat.domain.Blacklist;

public interface BlacklistCommandPort {
    void save(Blacklist blacklist);
}