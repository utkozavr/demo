package my.demo.graphqlgateway

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import graphql.GraphQL;
import graphql.Scalars.GraphQLBigDecimal
import graphql.execution.AsyncExecutionStrategy
import graphql.execution.SubscriptionExecutionStrategy
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring.newTypeWiring
import my.demo.client.currencyservice.CurrencyClient
import my.demo.client.currencyservice.CurrencyClientImp
import my.demo.client.eventservice.EventClient
import my.demo.client.eventservice.EventClientImp
import my.demo.client.exchangeservice.ExchangeClient
import my.demo.client.exchangeservice.ExchangeClientImp
import my.demo.config.Config
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Component
open class GraphQLProvider @Autowired constructor(private val graphQLDataFetchers: GraphQLDataFetchers) {

    private val graphQL: GraphQL = GraphQL
        .newGraphQL(buildSchema(readSchema()))
        .queryExecutionStrategy(AsyncExecutionStrategy())
        .subscriptionExecutionStrategy(SubscriptionExecutionStrategy())
        .build()

    private fun readSchema(name: String = "schema.graphqls"): String {
        return javaClass.classLoader.getResource(name).readText()
    }


    private fun buildSchema(sdl: String): GraphQLSchema {
        val typeRegistry = SchemaParser().parse(sdl)
        val runtimeWiring = buildWiring()
        val schemaGenerator = SchemaGenerator()
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring)
    }

    private fun buildWiring(): RuntimeWiring {
        return RuntimeWiring.newRuntimeWiring()
            .type(
                newTypeWiring("Query")
                    .dataFetcher("getCurrencies", graphQLDataFetchers.getCurrenciesDataFetcher())
            )
            .type(
                newTypeWiring("Mutation")
                    .dataFetcher("toggleDisabled", graphQLDataFetchers.getToggleDisabledDataFetcher())
            )
            .type(
                newTypeWiring("ExchangeRate")
                    .dataFetcher("destination", graphQLDataFetchers.getRateCurrenciesDataFetcher("destination"))
            )
            .type(
                newTypeWiring("ExchangeRate")
                    .dataFetcher("source", graphQLDataFetchers.getRateCurrenciesDataFetcher("source"))
            )
            .type(
                newTypeWiring("Subscription").dataFetchers(
                    mapOf(
                        "eventSubscription" to graphQLDataFetchers.getEventDataFetcher(),
                        "rateSubscription" to graphQLDataFetchers.getExchangeRateDataFetcher()
                    )
                )
            )
            .build()
    }

    @Bean
    fun graphQL(): GraphQL {
        return graphQL
    }

}