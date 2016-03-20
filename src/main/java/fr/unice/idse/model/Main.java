package fr.unice.idse.model;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import fr.unice.idse.model.card.Card;
import fr.unice.idse.model.card.Color;
import fr.unice.idse.model.regle.EffectCard;
import fr.unice.idse.model.regle.RuleChangeColor;
import fr.unice.idse.model.player.*;
public class Main
{
    public static void main(String[] args)
    {
        Player playerHostTest = new Player("PlayerHostTest","tok1");
        IAEasy playerTest2 = new IAEasy("Test","tok2",1);
//      IA playerTest3 = new IA("Toto","tok3",2);
//      IA playerTest4 = new IA("Toto","tok3",3);
        Game gameTest = new Game(playerHostTest, "GameTest", 3);
        gameTest.addPlayer(playerTest2);
//      gameTest.addPlayer(playerTest3);
//      gameTest.addPlayer(playerTest4);
        if (gameTest.start())
        {
            Board board = gameTest.getBoard();
            Alternative variante = gameTest.getAlternative();
            System.out.println("Debut de la partie");
            Player winner = null;
            while(!gameTest.gameEnd())
            {
                Player actualPlayer = board.getActualPlayer();
                Card actualCardInStack = board.getStack().topCard();
                System.out.println("Main du joueur " + actualPlayer.getName());
                for (int i = 0 ; i < actualPlayer.getCards().size() ; i++)
                {
                    Card card = actualPlayer.getCards().get(i);
                    System.out.println("[" + (i) + "] : " + card);
                }
                System.out.println("Carte dans la fosse " + actualCardInStack.toString());
                if(board.askPlayerCanPlay(actualPlayer))
                {
                    ArrayList<Card> playableCards = board.playableCards();
                    System.out.println("Carte jouable : " + playableCards.toString());
                    boolean played = false;
                    while(!played)
                    {
                        if(actualPlayer instanceof IAEasy) {
                        	((IAEasy) actualPlayer).reflexion(board);
                            //IA Reflexion
                        	if(gameTest.gameEnd())
                            {
                                winner = actualPlayer;
                                played = true;
                                //System.out.println("Le joueur joue : " + card);
                            }
                            else
                            {
                            played = true;
                            }
                        }
                        else {
                        	
                        	int numberCardsPlayer = actualPlayer.getCards().size();
                            System.out.println("Entrez une valeur qui est entre crochet pour jouer une carte.");
                            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
                            String input = "";
                            try
                            {
                                input = bufferRead.readLine();
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                            int numberCard = Integer.parseInt(input);
                            if(!input.equals("") && numberCard < actualPlayer.getCards().size())
                            {
                                Card card = actualPlayer.getCards().get(numberCard);
                                board.poseCard(card);
                                if(gameTest.gameEnd())
                                {
                                    winner = actualPlayer;
                                    played = true;
                                    System.out.println("Le joueur joue : " + card);
                                }
                                else
                                {
                                    if(numberCardsPlayer > actualPlayer.getCards().size())
                                    {
                                        played = true;
                                        System.out.println("Le joueur joue : " + card);
                                        if(actualPlayer.getCards().size() == 1)
                                        {
                                            System.out.println("Uno");
                                        }
                                        EffectCard effectCard = variante.getEffectCard(card);
                                        if(effectCard != null)
                                        {
                                            if(card.getColor().equals(Color.Black))
                                            {
                                                boolean chooseColor = false;
                                                while (!chooseColor)
                                                {
                                                    int choose = 0;
                                                    if(actualPlayer instanceof IA) {
                                                        //int choose = actualPlayer reflexion
                                                        choose = 1;
                                                    }
                                                    else {
                                                        System.out.println("Choisie une couleur :  1 pour Red, 2 pour Blue, 3 pour Yellow, 4 pour Green");
                                                        input = "";
                                                        try
                                                        {
                                                            input = bufferRead.readLine();
                                                        }
                                                        catch (IOException e)
                                                        {
                                                            e.printStackTrace();
                                                        }
                                                        choose = Integer.parseInt(input);
                                                    }
                                                    switch(choose)
                                                    {
                                                        case 1 :
                                                            effectCard.changeColor(Color.Red);
                                                            chooseColor = true;
                                                            System.out.println("Le joueur a choisie : Red");
                                                            break;
                                                        case 2 :
                                                            effectCard.changeColor(Color.Blue);
                                                            System.out.println("Le joueur a choisie : Blue");
                                                            chooseColor = true;
                                                            break;
                                                        case 3 :
                                                            effectCard.changeColor(Color.Yellow);
                                                            System.out.println("Le joueur a choisie : Yellow");
                                                            chooseColor = true;
                                                            break;
                                                        case 4 :
                                                            effectCard.changeColor(Color.Green);
                                                            System.out.println("Le joueur a choisie : Green");
                                                            chooseColor = true;
                                                            break;
                                                        default :
                                                            break;
                                                    }
                                                }
                                            }
                                            effectCard.action();
                                        }
                                        board.nextPlayer();
                                        if(effectCard != null && effectCard.getEffect())
                                        {
                                            effectCard.effect();
                                        }
                                    }
                                }
                            }
                        }
                        
                    }
                }
                else
                {
                    board.drawCard();
                    System.out.println("Le joueur a pioche : " + actualPlayer.getCards().get(actualPlayer.getCards().size()-1));
                    if(!board.askPlayerCanPlay(actualPlayer))
                    {
                        board.nextPlayer();
                    }
                }
            }
            System.out.println("Fin de la partie. Vainqueur : " + winner.getName());
        }
        else
        {
            System.out.println("Problème de chargement de la partie");
        }
    }
}
