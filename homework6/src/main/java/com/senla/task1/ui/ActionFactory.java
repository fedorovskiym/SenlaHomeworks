package com.senla.task1.ui;

public interface ActionFactory {

    IAction addMechanicAction();
    IAction removeMechanicById();
    IAction showMechanicsAction();
    IAction showSortedMechanicsByAlphabet();
    IAction showSortedMechanicsByBusy();
    IAction importMechanicsFromFileAction();
    IAction exportMechanicToFileAction();

    IAction addGaragePlaceAction();
    IAction showGaragePlacesAction();
    IAction removeGaragePlace();
    IAction importGaragePlaceFromFileAction();
    IAction exportGaragePlaceToFileAction();

    IAction createOrderAction();
    IAction getAvailableSlot();
    IAction acceptOrderAction();
    IAction closeOrderAction();
    IAction cancelOrderAction();
    IAction shiftOrderTimeAction();
    IAction deleteOrderAction();
    IAction findOrderByMechanicIdAction();
    IAction showOrdersByStatusAction();
    IAction showAllOrdersAction();
    IAction showSortedOrdersByDateOfSubmissionAction();
    IAction showSortedOrdersByDateOfCompletionAction();
    IAction showSortedOrdersByPriceAction();
    IAction showOrdersOverPeriodOfTimeAction();
    IAction showNearestAvailableSlot();
    IAction importOrderFromFileAction();
    IAction exportOrderToFileAction();

    IAction goBackAction();

}
