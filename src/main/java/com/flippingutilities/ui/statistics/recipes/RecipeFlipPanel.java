package com.flippingutilities.ui.statistics.recipes;

import com.flippingutilities.controller.FlippingPlugin;
import com.flippingutilities.model.RecipeFlip;
import com.flippingutilities.model.PartialOffer;
import com.flippingutilities.model.RecipeFlipGroup;
import com.flippingutilities.ui.uiutilities.CustomColors;
import com.flippingutilities.ui.uiutilities.Icons;
import com.flippingutilities.ui.uiutilities.TimeFormatters;
import com.flippingutilities.ui.uiutilities.UIUtilities;
import com.flippingutilities.utilities.Recipe;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.QuantityFormatter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The visual representation of a CombinationFlip. CombinationFlipPanels
 * are shown in the "combos" tab of the trade history section of an item in the
 * statistics tab.
 */
public class RecipeFlipPanel extends JPanel {
    private JLabel timeDisplay;
    private RecipeFlip recipeFlip;
    private FlippingPlugin plugin;
    private Recipe recipe;
    private RecipeFlipGroup recipeFlipGroup;

    public RecipeFlipPanel(RecipeFlipGroup recipeFlipGroup, RecipeFlip recipeFlip, Recipe recipe, FlippingPlugin plugin) {
        this.recipeFlipGroup = recipeFlipGroup;
        this.recipeFlip = recipeFlip;
        this.plugin = plugin;
        this.recipe = recipe;
        setBackground(CustomColors.DARK_GRAY);
        setBorder(new EmptyBorder(5,5,5,5));
        setLayout(new BorderLayout());

        timeDisplay = createTimeDisplay(recipeFlip);

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createProfitPanel(), BorderLayout.CENTER);
        add(createDetailsPanel(), BorderLayout.SOUTH);
    }


    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new DynamicGridLayout(2,1));
        titlePanel.setBackground(CustomColors.DARK_GRAY);


        String quantityInCombination = QuantityFormatter.formatNumber(recipeFlip.getRecipeCountMade(recipe));

        JLabel quantityLabel = new JLabel(quantityInCombination + "x");
        quantityLabel.setFont(FontManager.getRunescapeSmallFont());

        JPanel quantityAndTimePanel = new JPanel();
        quantityAndTimePanel.setBackground(CustomColors.DARK_GRAY);
        quantityAndTimePanel.add(quantityLabel);
        quantityAndTimePanel.add(timeDisplay);

        JLabel itemNameAndActionLabel = new JLabel(recipe.getName(), SwingConstants.CENTER);
        itemNameAndActionLabel.setFont(FontManager.getRunescapeSmallFont());
        itemNameAndActionLabel.setForeground(ColorScheme.GRAND_EXCHANGE_PRICE);

        titlePanel.add(itemNameAndActionLabel);
        titlePanel.add(quantityAndTimePanel);
        return titlePanel;
    }

    private JLabel createTimeDisplay(RecipeFlip combinationFlip) {
        JLabel timeDisplay = new JLabel(
                "(" + TimeFormatters.formatDurationTruncated(combinationFlip.getTimeOfCreation()) + " ago)",
                SwingConstants.CENTER);
        timeDisplay.setFont(FontManager.getRunescapeSmallFont());
        return timeDisplay;
    }

    private JPanel createComponentGroupPanel(Map<Integer, Map<String, PartialOffer>> partialOffers, boolean outputs) {

        JLabel titleLabel = new JLabel(outputs? "Outputs":"Inputs", SwingConstants.CENTER);
        titleLabel.setFont(FontManager.getRunescapeSmallFont());

        JPanel componentGroupPanel = new JPanel(new DynamicGridLayout(1 + partialOffers.size(), 1));
        componentGroupPanel.add(titleLabel);

        partialOffers.forEach((itemId, partialOfferMap) -> {
            componentGroupPanel.add(createComponentPanel(partialOfferMap));
        });

        return componentGroupPanel;
    }

    private JPanel createComponentPanel(Map<String, PartialOffer> partialOfferMap){
        List<PartialOffer> partialOfferList = new ArrayList<>(partialOfferMap.values());
        String itemName = partialOfferList.get(0).offer.getItemName();
        int quantity = partialOfferList.stream().mapToInt(po -> po.amountConsumed).sum();
        long avgPrice = partialOfferList.stream().mapToLong(po -> po.getOffer().getPrice()).sum()/quantity;

        JLabel itemNameLabel = new JLabel(itemName, SwingConstants.CENTER);
        itemNameLabel.setFont(FontManager.getRunescapeSmallFont());

        JPanel quantityPanel = new JPanel(new BorderLayout());
        JLabel quantityLabel = new JLabel("Quantity", SwingConstants.CENTER);
        quantityLabel.setFont(FontManager.getRunescapeSmallFont());
        JLabel quantityValueLabel = new JLabel(QuantityFormatter.formatNumber(quantity));
        quantityValueLabel.setFont(FontManager.getRunescapeSmallFont());
        quantityPanel.add(quantityLabel, BorderLayout.WEST);
        quantityPanel.add(quantityValueLabel, BorderLayout.EAST);

        JPanel pricePanel = new JPanel(new BorderLayout());
        JLabel priceLabel = new JLabel("Avg Price", SwingConstants.CENTER);
        priceLabel.setFont(FontManager.getRunescapeSmallFont());
        JLabel priceValueLabel = new JLabel(QuantityFormatter.formatNumber(avgPrice));
        priceValueLabel.setFont(FontManager.getRunescapeSmallFont());
        pricePanel.add(quantityLabel, BorderLayout.WEST);
        pricePanel.add(quantityValueLabel, BorderLayout.EAST);

        JPanel itemPanel = new JPanel(new DynamicGridLayout(3,1));
        itemPanel.add(itemNameLabel);
        itemPanel.add(quantityPanel);
        itemPanel.add(pricePanel);

        return itemPanel;
    }

    private JPanel createDetailsPanel() {

        JPanel inputPanel = createComponentGroupPanel(recipeFlip.getInputs(), false);
        JPanel outputPanel = createComponentGroupPanel(recipeFlip.getOutputs(), true);

        JPanel inputsAndOutputsPanel = new JPanel(new DynamicGridLayout(2,1));
        inputsAndOutputsPanel.setBorder(new EmptyBorder(5,0,0,0));
        inputsAndOutputsPanel.setBackground(CustomColors.DARK_GRAY);
        inputsAndOutputsPanel.setVisible(false);

        inputsAndOutputsPanel.add(inputPanel);
        inputsAndOutputsPanel.add(outputPanel);

        JLabel expandDetailsLabel = new JLabel("Expand Details", SwingConstants.CENTER);
        Color c = expandDetailsLabel.getForeground();
        expandDetailsLabel.setFont(FontManager.getRunescapeSmallFont());
        Font font=new Font(expandDetailsLabel.getFont().getName(),Font.ITALIC,expandDetailsLabel.getFont().getSize());
        expandDetailsLabel.setFont(font);
        UIUtilities.makeLabelUnderlined(expandDetailsLabel);
        expandDetailsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                inputsAndOutputsPanel.setVisible(!inputsAndOutputsPanel.isVisible());
                expandDetailsLabel.setText(inputsAndOutputsPanel.isVisible()? "Collapse Details": "Expand Details");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                expandDetailsLabel.setForeground(ColorScheme.GRAND_EXCHANGE_PRICE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                expandDetailsLabel.setForeground(c);
            }
        });

        JPanel expandDetailsPanel = new JPanel(new BorderLayout());
        expandDetailsPanel.setBorder(new EmptyBorder(8,0,0,0));
        expandDetailsPanel.setBackground(CustomColors.DARK_GRAY);
        expandDetailsPanel.add(expandDetailsLabel, BorderLayout.CENTER);
        expandDetailsPanel.add(createDeleteIcon(), BorderLayout.WEST);

        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(CustomColors.DARK_GRAY);
        detailsPanel.setBorder(new EmptyBorder(5,0,0,0));
        detailsPanel.add(expandDetailsPanel, BorderLayout.NORTH);
        detailsPanel.add(inputsAndOutputsPanel, BorderLayout.CENTER);

        return detailsPanel;
    }

    private JLabel createDeleteIcon() {
        JLabel deleteIcon = new JLabel(Icons.TRASH_CAN_OFF);
        deleteIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (plugin.getAccountCurrentlyViewed().equals(FlippingPlugin.ACCOUNT_WIDE)) {
                    JOptionPane.showMessageDialog(null, "You cannot delete combo flips in the Accountwide view");
                    return;
                }
                final int result = JOptionPane.showOptionDialog(deleteIcon, "Are you sure you want to delete this combo flip?",
                        "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                        null, new String[]{"Yes", "No"}, "No");

                if (result == JOptionPane.YES_OPTION) {
                    recipeFlipGroup.deleteFlip(recipeFlip);
                    plugin.getStatPanel().rebuildRecipesDisplay(plugin.viewRecipeFlipGroupsForCurrentView());
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                deleteIcon.setIcon(Icons.TRASH_CAN_ON);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                deleteIcon.setIcon(Icons.TRASH_CAN_OFF);
            }
        });

        return deleteIcon;
    }

    private JPanel createPartialOfferPanel(List<PartialOffer> partialOffers) {
        String itemName = partialOffers.get(0).offer.getItemName();
        long quantity = partialOffers.stream().mapToLong(po -> po.amountConsumed).sum();
        int itemPrice = (int) (partialOffers.stream().mapToLong(po -> po.offer.getPrice() * po.amountConsumed).sum()/quantity);

        JLabel itemNameLabel = new JLabel(itemName, SwingConstants.CENTER);
        itemNameLabel.setFont(FontManager.getRunescapeSmallFont());

        JLabel quantityDescLabel = new JLabel("Quantity:");
        JLabel quantityValueLabel = new JLabel(String.valueOf(quantity));

        JLabel priceDescLabel = new JLabel("Price ea:");
        JLabel priceValueLabel = new JLabel(QuantityFormatter.formatNumber(itemPrice) + " gp");

        List.of(quantityDescLabel, quantityValueLabel, priceDescLabel, priceValueLabel).forEach(l -> l.setFont(FontManager.getRunescapeSmallFont()));

        JPanel quantityPanel = new JPanel(new BorderLayout());
        quantityPanel.setBackground(CustomColors.DARK_GRAY);
        quantityPanel.add(quantityDescLabel, BorderLayout.WEST);
        quantityPanel.add(quantityValueLabel, BorderLayout.EAST);

        JPanel pricePanel = new JPanel(new BorderLayout());
        pricePanel.setBackground(CustomColors.DARK_GRAY);
        pricePanel.add(priceDescLabel, BorderLayout.WEST);
        pricePanel.add(priceValueLabel, BorderLayout.EAST);

        JPanel partialOfferPanel = new JPanel(new DynamicGridLayout(3,1));
        partialOfferPanel.add(itemNameLabel);
        partialOfferPanel.add(quantityPanel);
        partialOfferPanel.add(pricePanel);

        partialOfferPanel.setBackground(CustomColors.DARK_GRAY);

        return partialOfferPanel;
    }

    private JPanel createProfitPanel() {
        long quantity = recipeFlip.getRecipeCountMade(recipe);
        long profit = recipeFlip.getProfit();
        long profitEach = profit/quantity;
        String profitString = UIUtilities.quantityToRSDecimalStack(profit, true) + " gp";
        String profitEachString = quantity == 1? "": " (" + UIUtilities.quantityToRSDecimalStack(profitEach, false) + " gp ea)";
        String profitDescription = profit < 0? "Loss": "Profit:";

        JLabel profitValLabel = new JLabel(profitString + profitEachString);
        profitValLabel.setFont(FontManager.getRunescapeSmallFont());

        JLabel profitDescriptionLabel = new JLabel(profitDescription);
        profitDescriptionLabel.setFont(FontManager.getRunescapeSmallFont());

        profitDescriptionLabel.setForeground(profit >= 0? ColorScheme.GRAND_EXCHANGE_PRICE : CustomColors.OUTDATED_COLOR);
        profitValLabel.setForeground(profit >= 0? ColorScheme.GRAND_EXCHANGE_PRICE : CustomColors.OUTDATED_COLOR);

        JPanel profitPanel = new JPanel(new BorderLayout());
        profitPanel.setBackground(CustomColors.DARK_GRAY);

        profitPanel.add(profitDescriptionLabel, BorderLayout.WEST);
        profitPanel.add(profitValLabel, BorderLayout.EAST);

        return profitPanel;
    }

    public void updateTimeLabels() {
        timeDisplay.setText("(" + TimeFormatters.formatDurationTruncated(recipeFlip.getTimeOfCreation()) + " ago)");
    }
 }
