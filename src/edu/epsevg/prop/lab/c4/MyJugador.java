package edu.epsevg.prop.lab.c4;

import java.util.ArrayList;
import java.util.List;

/**
 * La classe MyJugador implementa la interfície Jugador i IAuto per crear un jugador que utilitza l'algorisme Minimax amb poda alfa-beta
 * per calcular el millor moviment en un joc de Tauler.
 * Aquesta classe també manté un comptador de càlculs heurístics realitzats.
 */
public class MyJugador implements Jugador, IAuto {
    public int maxDepth;
    public int heristicsCalculations = 0;

    /**
     * Constructor que inicialitza la profunditat màxima per al Minimax.
     *
     * @param depth profunditat màxima de recursió
     */
    public MyJugador(int depth) {
        this.maxDepth = depth;
    }

    /**
     * Realitza el moviment utilitzant l'algorisme Minimax amb poda alfa-beta.
     *
     * @param t    el tauler on es realitza el moviment
     * @param color el color de l'agent que fa el moviment
     * @return el moviment que ha de realitzar el jugador
     */
    @Override
    public int moviment(Tauler t, int color) {
        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;
        heristicsCalculations = 0;

        for (int col = 0; col < t.getMida(); col++) {
            if (t.movpossible(col)) {
                Tauler newBoard = new Tauler(t);
                newBoard.afegeix(col, color);

                int score = minimax(newBoard, maxDepth - 1, false, color, Integer.MIN_VALUE, Integer.MAX_VALUE);
                
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = col;
                }
            }
        }

        System.out.println("Càlculs heurístics: " + heristicsCalculations);
        
        return bestMove;
    }

    /**
     * Algorisme Minimax amb poda alfa-beta per calcular el millor moviment.
     *
     * @param board el tauler actual
     * @param depth la profunditat restant per explorar
     * @param isMaximizing si és el torn de maximitzar (jugador) o minimitzar (oponent)
     * @param color el color de l'agent
     * @param alpha el valor de poda alfa
     * @param beta el valor de poda beta
     * @return el valor heurístic del millor moviment
     */
    public int minimax(Tauler board, int depth, boolean isMaximizing, int color, int alpha, int beta) {
        if (depth == 0 || !board.espotmoure()) {
            heristicsCalculations++;
            return heuristic(board, color);
        }
    
        List<Integer> movimentsOrdenats = ordenarMoviments(board, color, isMaximizing);
    
        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < movimentsOrdenats.size(); i++) {
                int col = movimentsOrdenats.get(i);
                if (board.movpossible(col)) {
                    Tauler newBoard = new Tauler(board);
                    newBoard.afegeix(col, color);
    
                    int eval = minimax(newBoard, depth - 1, false, color, alpha, beta);
                    maxEval = Math.max(maxEval, eval);
                    alpha = Math.max(alpha, eval);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int i = 0; i < movimentsOrdenats.size(); i++) {
                int col = movimentsOrdenats.get(i);
                if (board.movpossible(col)) {
                    Tauler newBoard = new Tauler(board);
                    newBoard.afegeix(col, -color);
    
                    int eval = minimax(newBoard, depth - 1, true, color, alpha, beta);
                    //int eval = minimaxNoPoda(newBoard, depth - 1, true, color);

                    minEval = Math.min(minEval, eval);
                    beta = Math.min(beta, eval);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return minEval;
        }
    }
    
    /**
     * Ordena els possibles moviments en funció de la seva valoració heurística.
     *
     * @param board el tauler actual
     * @param color el color de l'agent
     * @param isMaximizing si és el torn de maximitzar o minimitzar
     * @return una llista ordenada dels moviments possibles
     */
    public List<Integer> ordenarMoviments(Tauler board, int color, boolean isMaximizing) {
        List<Integer> moviments = new ArrayList<>();
        for (int col = 0; col < board.getMida(); col++) {
            if (board.movpossible(col)) {
                moviments.add(col);
            }
        }
    
        moviments.sort((col1, col2) -> {
            int color1;
            int color2;
        
            if (isMaximizing) {
                color1 = color;
                color2 = color;
            } else {
                color1 = -color;
                color2 = -color;
            }
        
            Tauler board1 = new Tauler(board);
            board1.afegeix(col1, color1);
            int heuristica1 = heuristic(board1, color);
        
            Tauler board2 = new Tauler(board);
            board2.afegeix(col2, color2);
            int heuristica2 = heuristic(board2, color);
        
            if (isMaximizing) {
                return Integer.compare(heuristica2, heuristica1);
            } else {
                return Integer.compare(heuristica1, heuristica2);
            }
        });        
    
        return moviments;
    }    

    /**
     * Algorisme Minimax sense poda alfa-beta per calcular el millor moviment.
     *
     * @param board el tauler actual
     * @param depth la profunditat restant per explorar
     * @param isMaximizing si és el torn de maximitzar (jugador) o minimitzar (oponent)
     * @param color el color de l'agent
     * @return el valor heurístic del millor moviment
     */
    public int minimaxNoPoda(Tauler board, int depth, boolean isMaximizing, int color) {
        heristicsCalculations++;
        if (depth == 0 || !board.espotmoure()) {
            return heuristic(board, color);
        }
    
        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int col = 0; col < board.getMida(); col++) {
                if (board.movpossible(col)) {
                    Tauler newBoard = new Tauler(board);
                    newBoard.afegeix(col, color);
    
                    int eval = minimaxNoPoda(newBoard, depth - 1, false, color);
                    maxEval = Math.max(maxEval, eval);
                }
            }
            return maxEval;
        } 
        else {
            int minEval = Integer.MAX_VALUE;
            for (int col = 0; col < board.getMida(); col++) {
                if (board.movpossible(col)) {
                    Tauler newBoard = new Tauler(board);
                    newBoard.afegeix(col, -color);
    
                    int eval = minimaxNoPoda(newBoard, depth - 1, true, color);
                    minEval = Math.min(minEval, eval);
                }
            }
            return minEval;
        }
    }
    
    /**
     * Calcula la puntuació heurística d'un tauler.
     *
     * @param board el tauler actual
     * @param color el color de l'agent
     * @return la puntuació heurística del tauler
     */
    public int heuristic(Tauler board, int color) {
        int opponentColor = -color;
        int score = 0;

        for (int row = 0; row < board.getMida(); row++) {
            for (int col = 0; col < board.getMida(); col++) {
                int currentColor = board.getColor(row, col);

                if (currentColor == color) {
                    score += evaluatePosition(board, row, col, color);
                } else if (currentColor == opponentColor) {
                    score -= evaluatePosition(board, row, col, opponentColor);
                }
            }
        }

        score += controlCenter(board, color);

        return score;
    }

    /**
     * Avalua la posició d'una peça al tauler.
     *
     * @param board el tauler actual
     * @param row la fila on es troba la peça
     * @param col la columna on es troba la peça
     * @param color el color de la peça
     * @return la puntuació resultant de la posició de la peça
     */
    public int evaluatePosition(Tauler board, int row, int col, int color) {
        int score = 0;


        score += evaluateDirection(board, row, col, color, 1, 0);
        score += evaluateDirection(board, row, col, color, 0, 1);
        score += evaluateDirection(board, row, col, color, 1, 1);
        score += evaluateDirection(board, row, col, color, 1, -1);

        return score;
    }

    /**
     * Avalua la direcció d'una peça en una direcció específica.
     *
     * @param board el tauler actual
     * @param row la fila inicial
     * @param col la columna inicial
     * @param color el color de la peça
     * @param direccioRow la direcció de la fila
     * @param direccioCol la direcció de la columna
     * @return la puntuació resultant de la direcció evaluada
     */
    public int evaluateDirection(Tauler board, int row, int col, int color, int direccioRow, int direccioCol) {
        int size = board.getMida();
        int count = 0, buitsCount = 0, score = 0;

        for (int i = 0; i < 4; i++) {
            int newRow = row + i * direccioRow;
            int newCol = col + i * direccioCol;

            if (newRow >= 0 && newRow < size && newCol >= 0 && newCol < size) {
                int cellColor = board.getColor(newRow, newCol);
                if (cellColor == color) {
                    count++;
                } 
                else if (cellColor == 0) {
                    buitsCount++;
                } 
                else {
                    break;
                }
            } else {
                break;
            }
        }

        if (count + buitsCount >= 4) {
            if (count == 4)                   score += 1000;
            if (count == 3 && buitsCount > 0) score += 100;
            if (count == 2 && buitsCount > 0) score += 10;
            if (count == 1 && buitsCount > 0) score += 1;
        }

        return score;
    }

    /**
     * Calcula un bonus per controlar el centre del tauler.
     *
     * @param board el tauler actual
     * @param color el color de l'agent
     * @return el bonus de puntuació
     */
    public int controlCenter(Tauler board, int color) {
        int centerCol = board.getMida() / 2;
        int score = 0;

        for (int row = 0; row < board.getMida(); row++) {
            if (board.getColor(row, centerCol) == color) {
                score += 10;
            }
        }

        return score;
    }


    /**
     *
     * @return
     */
    @Override
    public String nom() {
        return "MyJugador";
    }
}
