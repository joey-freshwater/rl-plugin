package com.flippingutilities.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Used as the data structure that references OfferEvents in CombinationFlips. Its only purpose is to
 * show how much of an offer event was consumed as when creating combination flips you can
 * specify that you only want some of the offer to be used.
 */
@Data
@AllArgsConstructor
public class PartialOffer {
    public OfferEvent offer;
    public int amountConsumed;

    public PartialOffer clone() {
        return new PartialOffer(
                offer.clone(),
                amountConsumed
        );
    }

    /**
     * Returns an OfferEvent that represents the offer left after taking into account the amount the PartialOffer
     * consumed.
     */
    public OfferEvent toRemainingOfferEvent() {
        int remainingAmount = offer.getCurrentQuantityInTrade() - amountConsumed;
        OfferEvent adjustedOfferEvent = offer.clone();
        adjustedOfferEvent.setCurrentQuantityInTrade(remainingAmount);
        return adjustedOfferEvent;
    }

    public void hydrateUnderlyingOfferEvent(String madeBy, String itemName) {
        offer.setMadeBy(madeBy);
        offer.setItemName(itemName);
    }
}


