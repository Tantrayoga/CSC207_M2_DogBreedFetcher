package dogapi;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        String breed = "hound";
        BreedFetcher breedFetcher = new CachingBreedFetcher(new BreedFetcherForLocalTesting());

        try {
            int result = getNumberOfSubBreeds(breed, breedFetcher);
            System.out.println(breed + " has " + result + " sub breeds");
        } catch (BreedFetcher.BreedNotFoundException e) {
            System.out.println("Invalid breed: " + breed);
        }

        breed = "cat";
        try {
            int result = getNumberOfSubBreeds(breed, breedFetcher);
            System.out.println(breed + " has " + result + " sub breeds");
        } catch (BreedFetcher.BreedNotFoundException e) {
            System.out.println("Invalid breed: " + breed);
        }
    }

    /**
     * Return the number of sub breeds that the given dog breed has according to the
     * provided fetcher.
     * @param breed the name of the dog breed
     * @param breedFetcher the breedFetcher to use
     * @return the number of sub breeds. Zero should be returned if there are no sub breeds
     * returned by the fetcher
     * @throws BreedFetcher.BreedNotFoundException if the breed does not exist
     */
    public static int getNumberOfSubBreeds(String breed, BreedFetcher breedFetcher)
            throws BreedFetcher.BreedNotFoundException {
        List<String> subs = breedFetcher.getSubBreeds(breed); // may throw checked exception
        // If the fetcher returns an empty list, size() is 0, satisfying the spec.
        return (subs == null) ? 0 : subs.size();
    }
}
