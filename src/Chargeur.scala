import scala.io.Source

/**
 * Objet pour charger des graphes depuis des fichiers
 * Supporte deux représentations :
 *   - Matrice d'adjacence (dense)
 *   - Liste d'adjacence (sparse, plus efficace pour graphes creux)
 */
object Chargeur {
  
  /**
   * Lit un fichier et crée une matrice d'adjacence
   * 
   * Format attendu:
   *   ligne 1: nombre de sommets
   *   lignes suivantes: sommet_départ sommet_arrivée probabilité
   * 
   * @param chemin chemin du fichier (call by value)
   * @return Option[MatriceAdjacence]
   */
  def chargerMatrice(chemin: String): Option[MatriceAdjacence] = {
    try {
      val source = Source.fromFile(chemin)
      val lignes = source.getLines().toList
      source.close()
      
      if (lignes.isEmpty) {
        println(s"Erreur: fichier vide")
        return None
      }
      
      val nbSommets = lignes.head.trim.toInt
      println(s"Chargement: $nbSommets sommets")
      
      val matrice = new MatriceAdjacence(nbSommets)
      
      // Récursion terminale pour lire les arcs
      lireArcs(matrice, lignes.tail)
      
      Some(matrice)
      
    } catch {
      case e: Exception =>
        println(s"Erreur: ${e.getMessage}")
        None
    }
  }
  
  /**
   * Fonction récursive terminale pour lire les arcs
   * Utilise @annotation.tailrec pour optimisation
   */
  @annotation.tailrec
  private def lireArcs(matrice: MatriceAdjacence, lignes: List[String]): Unit = {
    lignes match {
      case Nil => // Fin
        
      case ligne :: reste =>
        val parties = ligne.trim.split("\\s+")
        if (parties.length == 3) {
          try {
            val depart = parties(0).toInt
            val arrivee = parties(1).toInt
            val proba = parties(2).toDouble
            matrice.set(depart, arrivee, proba)
          } catch {
            case _: Exception => // Ignorer les lignes malformées
          }
        }
        lireArcs(matrice, reste) // Récursion terminale
    }
  }
  
  /**
   * Crée une matrice depuis une liste d'arcs
   * Utilise HOF foreach avec lambda
   */
  def depuisMatrice(nbSommets: Int, arcs: List[(Int, Int, Double)]): MatriceAdjacence = {
    val matrice = new MatriceAdjacence(nbSommets)
    arcs.foreach { case (d, a, p) => matrice.set(d, a, p) }
    matrice
  }
  
  /**
   * Lit un fichier et crée une liste d'adjacence
   * 
   * Format attendu:
   *   ligne 1: nombre de sommets
   *   lignes suivantes: sommet_départ sommet_arrivée probabilité
   * 
   * @param chemin chemin du fichier (call by value)
   * @return Option[ListeAdjacence]
   */
  def chargerListe(chemin: String): Option[ListeAdjacence] = {
    try {
      val source = Source.fromFile(chemin)
      val lignes = source.getLines().toList
      source.close()
      
      if (lignes.isEmpty) {
        println(s"Erreur: fichier vide")
        return None
      }
      
      val nbSommets = lignes.head.trim.toInt
      println(s"Chargement: $nbSommets sommets")
      
      val liste = new ListeAdjacence(nbSommets)
      
      // Récursion terminale pour lire les arcs
      lireArcsListe(liste, lignes.tail)
      
      Some(liste)
      
    } catch {
      case e: Exception =>
        println(s"Erreur: ${e.getMessage}")
        None
    }
  }
  
  /**
   * Fonction récursive terminale pour lire les arcs (liste d'adjacence)
   */
  @annotation.tailrec
  private def lireArcsListe(liste: ListeAdjacence, lignes: List[String]): Unit = {
    lignes match {
      case Nil => // Fin
        
      case ligne :: reste =>
        val parties = ligne.trim.split("\\s+")
        if (parties.length == 3) {
          try {
            val depart = parties(0).toInt
            val arrivee = parties(1).toInt
            val proba = parties(2).toDouble
            liste.set(depart, arrivee, proba)
          } catch {
            case _: Exception => // Ignorer les lignes malformées
          }
        }
        lireArcsListe(liste, reste) // Récursion terminale
    }
  }
  
  /**
   * Alias pour chargerMatrice (compatibilité)
   */
  def charger(chemin: String): Option[Graphe] = 
    chargerMatrice(chemin).map(m => m: Graphe)
  
  /**
   * Alias pour depuisMatrice (compatibilité)
   */
  def depuis(nbSommets: Int, arcs: List[(Int, Int, Double)]): MatriceAdjacence = 
    depuisMatrice(nbSommets, arcs)
  
  /**
   * Crée une liste d'adjacence depuis une liste d'arcs
   * Utilise HOF foreach avec lambda
   */
  def depuisListe(nbSommets: Int, arcs: List[(Int, Int, Double)]): ListeAdjacence = {
    val liste = new ListeAdjacence(nbSommets)
    arcs.foreach { case (d, a, p) => liste.set(d, a, p) }
    liste
  }
}
