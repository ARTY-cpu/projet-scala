/**
 * Liste d'adjacence pour un graphe de Markov
 * 
 * Représentation plus efficace qu'une matrice pour les graphes creux
 * (beaucoup de zéros). Utilise un Map de listes pour stocker uniquement
 * les transitions non-nulles.
 * 
 * Structure : Map[sommet -> List[(destination, probabilité)]]
 * 
 * Implémente Graphe (abstraction non générique) et Matrice[Double] (générique)
 * 
 * Exemple pour le graphe exemple1.txt :
 *   Sommet 1 : [(1, 0.95), (2, 0.04), (3, 0.01)]
 *   Sommet 2 : [(2, 0.90), (3, 0.05), (4, 0.05)]
 *   Sommet 3 : [(3, 0.80), (4, 0.20)]
 *   Sommet 4 : [(1, 1.00)]
 * 
 * @param n nombre de sommets
 */
class ListeAdjacence(val n: Int) extends Graphe with Matrice[Double] {
  
  // Utilise un Map pour stocker les listes d'adjacence
  // Clé = sommet de départ, Valeur = List de (destination, probabilité)
  private var adjacences: Map[Int, List[(Int, Double)]] = Map()
  
  // Initialisation : tous les sommets ont des listes vides
  initialiser()
  
  /** Initialise toutes les listes d'adjacence à vide */
  private def initialiser(): Unit = {
    adjacences = (1 to n).map(sommet => sommet -> List.empty[(Int, Double)]).toMap
  }
  
  /**
   * Récupère la probabilité de transition i → j
   * Utilise HOF find avec lambda
   */
  def get(i: Int, j: Int): Double = {
    require(estValide(i) && estValide(j), s"Indices invalides: ($i, $j)")
    
    // Recherche dans la liste d'adjacence du sommet i
    adjacences.getOrElse(i, List.empty)
      .find { case (dest, _) => dest == j }  // HOF find avec lambda
      .map { case (_, proba) => proba }      // HOF map pour extraire la probabilité
      .getOrElse(0.0)                        // 0 si pas de transition
  }
  
  /**
   * Définit la probabilité de transition i → j
   * Utilise HOF filterNot et opérateur ::
   */
  def set(i: Int, j: Int, valeur: Double): Unit = {
    require(estValide(i) && estValide(j), s"Indices invalides: ($i, $j)")
    require(valeur >= 0 && valeur <= 1, s"Probabilité invalide: $valeur")
    
    val listeActuelle = adjacences.getOrElse(i, List.empty)
    
    if (valeur > 0) {
      // Ajoute ou remplace la transition
      // Supprime l'ancienne valeur si elle existe, puis ajoute la nouvelle
      val nouvelleListe = (j, valeur) :: listeActuelle.filterNot { case (dest, _) => dest == j }
      adjacences = adjacences.updated(i, nouvelleListe)
    } else {
      // Supprime la transition si probabilité = 0
      val nouvelleListe = listeActuelle.filterNot { case (dest, _) => dest == j }
      adjacences = adjacences.updated(i, nouvelleListe)
    }
  }
  
  // Implémentation de l'interface Graphe (abstraction non générique)
  def nbSommets: Int = n
  def proba(i: Int, j: Int): Double = get(i, j)
  def setProba(i: Int, j: Int, p: Double): Unit = set(i, j, p)
  
  def taille: Int = n
  
  /** Vérifie si un indice de sommet est valide */
  private def estValide(sommet: Int): Boolean = sommet >= 1 && sommet <= n
  
  /** 
   * Affiche la liste d'adjacence 
   * Format : Sommet X : [(dest1, proba1), (dest2, proba2), ...]
   */
  def afficher(): Unit = {
    println(s"\nListe d'adjacence ($n sommets):")
    println("-" * 50)
    
    for (sommet <- 1 to n) {
      val liste = adjacences.getOrElse(sommet, List.empty)
      
      if (liste.isEmpty) {
        println(s"Sommet $sommet : []")
      } else {
        // Trie la liste par destination pour un affichage cohérent
        val listeTriee = liste.sortBy { case (dest, _) => dest }
        val str = listeTriee.map { case (dest, proba) => 
          f"($dest, $proba%.2f)" 
        }.mkString(", ")
        println(s"Sommet $sommet : [$str]")
      }
    }
    println()
  }
  
  /**
   * Affiche la représentation graphique classique avec flèches
   * Format : [head @] -> (dest1, proba1) @-> (dest2, proba2) @-> ...
   */
  def afficherGraphique(): Unit = {
    println(s"\nReprésentation graphique des listes chaînées:")
    println("-" * 50)
    
    for (sommet <- 1 to n) {
      val liste = adjacences.getOrElse(sommet, List.empty)
      
      print(s"Sommet $sommet : [head @]")
      
      if (liste.isEmpty) {
        println(" -> None")
      } else {
        // Trie par destination pour cohérence
        val listeTriee = liste.sortBy { case (dest, _) => dest }.reverse
        for ((dest, proba) <- listeTriee) {
          print(f" -> ($dest, $proba%.2f) @")
        }
        println()
      }
    }
    println()
  }
  
  /**
   * Calcule la somme des probabilités sortantes d'un sommet
   * Utilise HOF map et sum
   */
  def sommeSortante(sommet: Int): Double = {
    require(estValide(sommet), s"Sommet invalide: $sommet")
    adjacences.getOrElse(sommet, List.empty)
      .map { case (_, proba) => proba }  // Extrait les probabilités
      .sum                                // Somme
  }
  
  /**
   * Vérifie si c'est un graphe de Markov valide
   * Utilise HOF forall avec lambda
   */
  def estValide(epsilon: Double = 0.0001): Boolean = {
    (1 to n).forall { sommet =>
      val somme = sommeSortante(sommet)
      math.abs(somme - 1.0) < epsilon || somme == 0.0
    }
  }
  
  /** Affiche le rapport de validation */
  def afficherValidation(epsilon: Double = 0.0001): Unit = {
    println("\n=== Validation du graphe de Markov ===")
    
    var valide = true
    for (sommet <- 1 to n) {
      val somme = sommeSortante(sommet)
      if (somme > 0) {
        val ok = math.abs(somme - 1.0) < epsilon
        val statut = if (ok) "ok" else "ko"
        println(f"Sommet $sommet%2d : somme = $somme%.4f $statut")
        if (!ok) valide = false
      }
    }
    
    println("\n" + (if (valide) "VALIDE" else "INVALIDE"))
    println("=" * 40 + "\n")
  }
  
  /**
   * Retourne les successeurs d'un sommet
   * Utilise HOF sortBy
   */
  def successeurs(sommet: Int): List[(Int, Double)] = {
    require(estValide(sommet), s"Sommet invalide: $sommet")
    adjacences.getOrElse(sommet, List.empty).sortBy { case (dest, _) => dest }
  }
}
