package mu.rekolt.service;

import mu.rekolt.model.Delivery;
import mu.rekolt.model.Grade;
import mu.rekolt.model.Member;

import java.util.*;

public class SeasonService {
    private final List<Delivery> deliveries = new ArrayList<>();
    private final Map<String, Member> members = new HashMap<>();
    private final Map<String, Double> totalPaymentPerMember = new HashMap<>();
    private final Map<String, List<Delivery>> deliveriesPerMember = new HashMap<>();
    private final Set<String> memberIds = new HashSet<>();
    private final double[][] weeklyGrid = new double[20][4]; // rows = weeks 1-20, cols = MZE/BNS/POT/TEA
    private final String[] produceOrder = {"MZE", "BNS", "POT", "TEA"};

    public void recordDelivery(Delivery delivery) {
        deliveries.add(delivery);

        Member member = delivery.getMember();
        members.put(member.getId(), member);
        memberIds.add(member.getId());

        double net = delivery.calculateNetPayable();
        totalPaymentPerMember.merge(member.getId(), net, Double::sum);

        deliveriesPerMember
                .computeIfAbsent(member.getId(), k -> new ArrayList<>())
                .add(delivery);

        int col = indexOfProduce(delivery.getProduce().getCode());
        int row = delivery.getWeek() - 1;
        weeklyGrid[row][col] += delivery.getMassKg();
    }

    private int indexOfProduce(String code) {
        for (int i = 0; i < produceOrder.length; i++) {
            if (produceOrder[i].equals(code)) return i;
        }
        throw new IllegalArgumentException("Unknown produce code: " + code);
    }

    public List<Delivery> getDeliveries() { return deliveries; }
    public Map<String, Double> getTotalPaymentPerMember() { return totalPaymentPerMember; }
    public Map<String, List<Delivery>> getDeliveriesPerMember() { return deliveriesPerMember; }
    public Set<String> getMemberIds() { return memberIds; }
    public Map<String, Member> getMembers() { return members; }
    public double[][] getWeeklyGrid() { return weeklyGrid; }
    public String[] getProduceOrder() { return produceOrder; }

    // Search by identifier, handling the absent case cleanly
    public Optional<Delivery> findDeliveryById(String id) {
        for (Delivery d : deliveries) {
            if (d.getId().equals(id)) {
                return Optional.of(d);
            }
        }
        return Optional.empty();
    }

    // Comparator: sort by net payable, descending
    public List<Delivery> topDeliveriesByValue(int limit) {
        List<Delivery> sorted = new ArrayList<>(deliveries);
        sorted.sort(Comparator.comparingDouble(Delivery::calculateNetPayable).reversed());
        return sorted.subList(0, Math.min(limit, sorted.size()));
    }

    // Comparable: natural ordering, by ID
    public List<Delivery> sortedById() {
        List<Delivery> sorted = new ArrayList<>(deliveries);
        Collections.sort(sorted);
        return sorted;
    }

    // Iterator: removes REJECT deliveries from a working copy, keeps the master list intact
    public List<Delivery> nonRejectedDeliveries() {
        List<Delivery> working = new ArrayList<>(deliveries);
        Iterator<Delivery> it = working.iterator();
        while (it.hasNext()) {
            if (it.next().getGrade() == Grade.REJECT) {
                it.remove();
            }
        }
        return working;
    }
}