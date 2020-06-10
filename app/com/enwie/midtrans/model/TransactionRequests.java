package com.enwie.midtrans.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import models.SalesOrder;

/**
 * @author hendriksaragih
 */
public class TransactionRequests {
    @JsonProperty("transaction_details")
    private TransactionDetailRequests transactionDetails;

    public TransactionRequests(){}

    public TransactionRequests(SalesOrder so){
        TransactionDetailRequests transactionDetails = new TransactionDetailRequests();
        transactionDetails.setOrderId(so.orderNumber);
        transactionDetails.setGrossAmount(so.totalPrice.intValue());

        setTransactionDetails(transactionDetails);
    }


    public TransactionDetailRequests getTransactionDetails() {
        return transactionDetails;
    }

    public void setTransactionDetails(TransactionDetailRequests transactionDetails) {
        this.transactionDetails = transactionDetails;
    }
}
