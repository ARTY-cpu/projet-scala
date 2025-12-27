/**
 * Abstraction non générique pour un graphe de Markov
 * 
 * Cette abstraction permet de manipuler un graphe sans connaître
 * sa représentation interne (matrice ou liste d'adjacence).
 * 
 * Un graphe de Markov a les propriétés suivantes :
 * - Ensemble de sommets numérotés de 1 à n
 * - Transitions probabilistes entre sommets
 * - Somme des probabilités sortantes = 1 pour chaque sommet
 */
trait Graphe {
  
  /**
   * Retourne le nombre de sommets du graphe
   */
  def nbSommets: Int
  
  /**
   * Retourne la probabilité de transition du sommet i vers le sommet j
   * @param i sommet de départ (base 1)
   * @param j sommet d'arrivée (base 1)
   * @return probabilité entre 0 et 1
   */
  def proba(i: Int, j: Int): Double
  
  /**
   * Définit la probabilité de transition du sommet i vers le sommet j
   * @param i sommet de départ (base 1)
   * @param j sommet d'arrivée (base 1)
   * @param p probabilité entre 0 et 1
   */
  def setProba(i: Int, j: Int, p: Double): Unit
  
  /**
   * Retourne la liste des successeurs d'un sommet avec leurs probabilités
   * @param sommet numéro du sommet (base 1)
   * @return liste de (destination, probabilité)
   */
  def successeurs(sommet: Int): List[(Int, Double)]
  
  /**
   * Calcule la somme des probabilités sortantes d'un sommet
   * @param sommet numéro du sommet (base 1)
   * @return somme des probabilités
   */
  def sommeSortante(sommet: Int): Double
  
  /**
   * Vérifie si le graphe est un graphe de Markov valide
   * @param epsilon marge d'erreur pour la validation
   * @return true si valide, false sinon
   */
  def estValide(epsilon: Double = 0.0001): Boolean
  
  /**
   * Affiche le graphe
   */
  def afficher(): Unit
  
  /**
   * Affiche un rapport de validation du graphe
   */
  def afficherValidation(epsilon: Double = 0.0001): Unit
}
