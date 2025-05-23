package hr.java.scrabble.config.jndi;

import lombok.Getter;

@Getter
public enum ConfigurationKey {

    GAME_SIMPLIFIED("game.simplified"),
    HOST("host"),
    SERVER_PORT("server.port"),
    SERVER_RMI_PORT("server.rmi.port"),
    RANDOM_PORT_HINT("random.port.hint"),
    API_KEY("api.key"),
    API_URL("api.url"),
    X_API_KEY("x.api.key"),
    DO_API_VALIDATIONS("do.api.validations"),
    GA_POPULATION_SIZE("ga.population.size"),
    ELITISM_RATE("ga.elitism.rate"),
    MUTATION_RATE("ga.mutation.rate");

    private final String key;

    ConfigurationKey(String key) {
        this.key = key;
    }

}
