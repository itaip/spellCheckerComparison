package com.itai.spellcheck.ebayspellchecker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.xml.ws.Response;

import com.ebay.marketplace.search.v1.services.findingrecommendationservice.AssociatedSearchesResponse;
import com.ebay.marketplace.search.v1.services.findingrecommendationservice.FindingRecommendationRequest;
import com.ebay.marketplace.search.v1.services.findingrecommendationservice.KeywordBasedSearchesRequest;
import com.ebay.marketplace.services.AckValue;
import com.ebay.marketplace.services.findingrecommendationservice.findingrecommendationservice.gen.SharedFindingRecommendationServiceConsumer;
import com.ebay.soaframework.common.exceptions.ServiceException;
import com.google.gson.Gson;
import com.itai.spellcheck.common.Pair;
import com.itai.spellcheck.common.SpellChecker;

/**
 * @author Itai Peleg
 *         Created on 11/12/2014.
 */
public class EbaySpellChecker implements SpellChecker {
    FindingRecommendationRequest request;
    SharedFindingRecommendationServiceConsumer consumer;

    public AssociatedSearchesResponse associatedSearch(KeywordBasedSearchesRequest request, String poolType) throws ServiceException {

        SharedFindingRecommendationServiceConsumer consumer = getConsumer(poolType);
        AssociatedSearchesResponse response = consumer.getSpellCorrections(request);

        return response;

    }

    public Response<AssociatedSearchesResponse> associatedSearchAsync(KeywordBasedSearchesRequest request, String poolType)
            throws ServiceException {
        long start = System.nanoTime();
        SharedFindingRecommendationServiceConsumer consumer = getConsumer(poolType);
        Response<AssociatedSearchesResponse> response = consumer.getSpellCorrectionsAsync(request);
        System.out.println(" - time= " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) + "ms ");
        return response;

    }

    public List<AssociatedSearchesResponse> associatedSearchAsync(List<KeywordBasedSearchesRequest> requests, String poolType)
            throws ServiceException {
        long start = System.nanoTime();
        List<Response<AssociatedSearchesResponse>> futures = new ArrayList<>(requests.size());
        for (KeywordBasedSearchesRequest req : requests) {
            long a = System.nanoTime();
            futures.add(getConsumer(poolType).getSpellCorrectionsAsync(req));
            long b = System.nanoTime();
            System.out.printf("add request= %d\n", TimeUnit.NANOSECONDS.toMillis(b - a));
        }
        long mid = System.nanoTime();
        System.out.printf("add all request= %d\n", TimeUnit.NANOSECONDS.toMillis(mid - start));
        List<AssociatedSearchesResponse> response = new ArrayList<>(futures.size());
        for (Response<AssociatedSearchesResponse> future : futures) {
            long a = System.nanoTime();
            try {
                response.add(future.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
            long b = System.nanoTime();
            System.out.printf("get response= %d\n", TimeUnit.NANOSECONDS.toMillis(b - a));
        }
        System.out.printf("get All response= %d\n", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - mid));
        System.out.println(" - time= " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) + "ms ");
        return response;

    }

    private SharedFindingRecommendationServiceConsumer getConsumer(String poolType) throws ServiceException {
        SharedFindingRecommendationServiceConsumer consumer =
                new SharedFindingRecommendationServiceConsumer("SharedFindingRecommendationServiceConsumer",
                        poolType, EbaySpellChecker.class, false);
        consumer.getServiceInvokerOptions().setConsumerId("itai peleg");
        return consumer;
    }

    public String toString(AssociatedSearchesResponse response) {
        Gson gson = new Gson();
        return gson.toJson(response);
    }

    @Override
    public Pair<String, String> spellCheckOneToken(String oneToken) throws Exception {
        long start = System.nanoTime();
        Pair<String, String> result = null;

        KeywordBasedSearchesRequest request = new KeywordBasedSearchesRequest();
        request.setGlobalId("EBAY-US");
        request.setKeywords(oneToken);
        AssociatedSearchesResponse r1 = associatedSearch(request, "dev");
        if (r1.getAck().equals(AckValue.SUCCESS) && !r1.getResponse().isEmpty()) {
            result = new Pair<>(r1.getResponse().get(0).getOriginalKeywords(), r1.getResponse().get(0)
                    .getRecommendedKeywords());
        }
        System.out.printf("spellCheck took %dms, %s\n", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() -
                start), result);

        return result;
    }

    @Override
    public List<Pair<String, String>> spellCheck(String input) throws Exception {
        return null;
    }
}
