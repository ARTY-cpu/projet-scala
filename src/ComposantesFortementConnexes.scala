/**
 * Module pour la détection des composantes fortement connexes (CFC)
 * et l'analyse des propriétés des classes dans les graphes de Markov
 * 
 * Implémente l'algorithme de Tarjan pour trouver les CFC
 * et des fonctions pour analyser les distributions stationnaires par classe
 */

import scala.collection.mutable
import scala.util.boundary

/**
 * Représente une partition du graphe en composantes fortement connexes
 */
case class Partition(composantes: List[Set[Int]]) {
  
  /**
   * Nombre de composantes dans la partition
   */
  def nbComposantes: Int = composantes.length
  
  /**
   * Retourne la composante contenant un sommet donné
   */
  def composanteDe(sommet: Int): Option[Set[Int]] = {
    composantes.find(_.contains(sommet))
  }
  
  /**
   * Retourne l'index de la composante contenant un sommet
   */
  def indexComposanteDe(sommet: Int): Option[Int] = {
    composantes.indexWhere(_.contains(sommet)) match {
      case -1 => None
      case i => Some(i)
    }
  }
  
  /**
   * Affiche la partition
   */
  def afficher(): Unit = {
    println("\n=== PARTITION DU GRAPHE ===")
    println(s"Nombre de composantes : $nbComposantes\n")
    
    composantes.zipWithIndex.foreach { case (comp, idx) =>
      val sommets = comp.toList.sorted.mkString(", ")
      println(f"Composante C${idx + 1}: {$sommets}")
    }
    println()
  }
}

/**
 * Type de classe dans un graphe de Markov
 */
sealed trait TypeClasse
case object Transitoire extends TypeClasse  // On peut sortir de la classe
case object Persistante extends TypeClasse  // On ne peut pas sortir (fermée)

/**
 * Objet pour détecter les composantes fortement connexes
 * et analyser les propriétés des classes
 */
object ComposantesFortementConnexes {
  
  /**
   * Détecte les composantes fortement connexes avec l'algorithme de Tarjan
   * 
   * Algorithme de Tarjan :
   * - Parcours en profondeur du graphe
   * - Utilise deux indices : index (ordre de visite) et lowlink (plus petit index accessible)
   * - Une composante est formée quand lowlink[v] == index[v]
   * 
   * @param graphe graphe à analyser
   * @return partition du graphe en CFC
   */
  def tarjan(graphe: Graphe): Partition = {
    val n = graphe.nbSommets
    
    // Structures de données pour l'algorithme
    val index = mutable.Map[Int, Int]()      // Index de visite
    val lowlink = mutable.Map[Int, Int]()    // Plus petit index accessible
    val enPile = mutable.Set[Int]()          // Sommets dans la pile
    val pile = mutable.Stack[Int]()          // Pile pour DFS
    var indexCourant = 0                     // Compteur d'index
    val composantes = mutable.ListBuffer[Set[Int]]()
    
    /**
     * Fonction récursive de parcours en profondeur
     */
    def strongConnect(v: Int): Unit = {
      // Initialisation du sommet v
      index(v) = indexCourant
      lowlink(v) = indexCourant
      indexCourant += 1
      pile.push(v)
      enPile.add(v)
      
      // Explorer les successeurs
      val successeurs = graphe.successeurs(v)
      for ((w, _) <- successeurs) {
        if (!index.contains(w)) {
          // w n'a pas encore été visité
          strongConnect(w)
          lowlink(v) = math.min(lowlink(v), lowlink(w))
        } else if (enPile.contains(w)) {
          // w est dans la pile, donc dans la composante courante
          lowlink(v) = math.min(lowlink(v), index(w))
        }
      }
      
      // Si v est une racine de composante
      if (lowlink(v) == index(v)) {
        val composante = mutable.Set[Int]()
        
        // Dépiler jusqu'à v pour former la composante
        var w = pile.pop()
        enPile.remove(w)
        composante.add(w)
        
        while (w != v) {
          w = pile.pop()
          enPile.remove(w)
          composante.add(w)
        }
        
        composantes += composante.toSet
      }
    }
    
    // Lancer le parcours pour chaque sommet non visité
    for (v <- 1 to n if !index.contains(v)) {
      strongConnect(v)
    }
    
    Partition(composantes.toList.reverse)
  }
  
  /**
   * Détermine si une classe est transitoire ou persistante
   * 
   * Une classe est persistante (fermée) si aucun sommet de la classe
   * n'a de transition vers un sommet hors de la classe
   * 
   * @param graphe graphe à analyser
   * @param classe ensemble des sommets de la classe
   * @return type de la classe
   */
  def typeClasse(graphe: Graphe, classe: Set[Int]): TypeClasse = {
    // Vérifier s'il existe une transition sortante
    boundary {
      for (sommet <- classe) {
        val successeurs = graphe.successeurs(sommet)
        for ((dest, proba) <- successeurs if proba > 0) {
          if (!classe.contains(dest)) {
            // Transition vers l'extérieur de la classe
            boundary.break(Transitoire)
          }
        }
      }
      Persistante
    }
  }
  
  /**
   * Extrait une sous-matrice correspondant à une classe donnée
   * 
   * La sous-matrice ne contient que les lignes et colonnes
   * des sommets appartenant à la classe
   * 
   * @param matrice matrice d'adjacence du graphe complet
   * @param partition partition du graphe en CFC
   * @param indexClasse index de la classe à extraire (base 0)
   * @return sous-matrice de la classe
   */
  def extraireSousMatrice(
    matrice: MatriceAdjacence, 
    partition: Partition, 
    indexClasse: Int
  ): MatriceAdjacence = {
    require(indexClasse >= 0 && indexClasse < partition.nbComposantes,
      s"Index de classe invalide: $indexClasse (max: ${partition.nbComposantes - 1})")
    
    val classe = partition.composantes(indexClasse)
    val sommetsOrdonnes = classe.toList.sorted
    val taille = sommetsOrdonnes.length
    
    // Créer la sous-matrice
    val sousMatrice = new MatriceAdjacence(taille)
    
    // Mapping : ancien numéro de sommet -> nouveau numéro (1 à taille)
    val mapping = sommetsOrdonnes.zipWithIndex.map { case (ancien, idx) => 
      ancien -> (idx + 1) 
    }.toMap
    
    // Copier les probabilités
    for {
      (ancienI, i) <- sommetsOrdonnes.zipWithIndex
      (ancienJ, j) <- sommetsOrdonnes.zipWithIndex
    } {
      val proba = matrice.proba(ancienI, ancienJ)
      sousMatrice.setProba(i + 1, j + 1, proba)
    }
    
    sousMatrice
  }
  
  /**
   * Calcule la distribution stationnaire d'une classe
   * 
   * Pour une classe persistante : cherche la convergence de M^n
   * Pour une classe transitoire : retourne une distribution nulle
   * 
   * @param graphe graphe complet
   * @param partition partition en CFC
   * @param indexClasse index de la classe
   * @param epsilon seuil de convergence
   * @return distribution stationnaire (ou None si pas de convergence)
   */
  def distributionStationnaireClasse(
    graphe: Graphe,
    partition: Partition,
    indexClasse: Int,
    epsilon: Double = 0.01
  ): Option[Distribution] = {
    
    val classe = partition.composantes(indexClasse)
    val matrice = graphe match {
      case m: MatriceAdjacence => m
      case l: ListeAdjacence => CalculsMatriciels.listeVersMatrice(l)
    }
    
    // Vérifier le type de classe
    val typeC = typeClasse(graphe, classe)
    
    typeC match {
      case Transitoire =>
        // Distribution nulle pour classe transitoire
        val probas = Vector.fill(graphe.nbSommets)(0.0)
        Some(Distribution.depuisVecteur(probas))
        
      case Persistante =>
        // Extraire la sous-matrice de la classe
        val sousMatrice = extraireSousMatrice(matrice, partition, indexClasse)
        
        // Chercher la convergence
        val (n, mn, converge) = CalculsMatriciels.trouverConvergence(
          sousMatrice, 
          epsilon
        )
        
        if (converge) {
          // Extraire la distribution depuis la première ligne de M^n
          val distClasse = Distribution.depuisLigneMatrice(mn, 1)
          
          // Créer une distribution sur le graphe complet
          // (0 partout sauf sur les sommets de la classe)
          val sommetsOrdonnes = classe.toList.sorted
          val probasCompletes = (1 to graphe.nbSommets).map { sommet =>
            sommetsOrdonnes.indexOf(sommet) match {
              case -1 => 0.0  // Sommet hors de la classe
              case idx => distClasse(idx + 1)  // Sommet dans la classe
            }
          }.toVector
          
          Some(Distribution.depuisVecteur(probasCompletes))
        } else {
          None  // Pas de convergence
        }
    }
  }
  
  /**
   * Affiche les distributions stationnaires de toutes les classes
   * 
   * @param graphe graphe à analyser
   * @param partition partition en CFC
   */
  def afficherDistributionsStationnaires(
    graphe: Graphe,
    partition: Partition
  ): Unit = {
    println("\n" + "=" * 70)
    println("DISTRIBUTIONS STATIONNAIRES PAR CLASSE")
    println("=" * 70)
    
    val matrice = graphe match {
      case m: MatriceAdjacence => m
      case l: ListeAdjacence => CalculsMatriciels.listeVersMatrice(l)
    }
    
    for ((classe, idx) <- partition.composantes.zipWithIndex) {
      val typeC = typeClasse(graphe, classe)
      val sommets = classe.toList.sorted.mkString(", ")
      
      println(f"\n>>> Composante C${idx + 1}: {$sommets}")
      println(f"    Type: ${typeC match {
        case Transitoire => "TRANSITOIRE (on peut sortir)"
        case Persistante => "PERSISTANTE (fermée)"
      }}")
      
      typeC match {
        case Transitoire =>
          println("    Distribution stationnaire: NULLE (tous les états → 0)")
          
        case Persistante =>
          val distOpt = distributionStationnaireClasse(graphe, partition, idx)
          
          distOpt match {
            case Some(dist) =>
              println("    Distribution stationnaire:")
              
              // Afficher uniquement les probabilités non nulles
              val sommetsOrdonnes = classe.toList.sorted
              for ((sommet, i) <- sommetsOrdonnes.zipWithIndex) {
                val proba = dist(sommet)
                println(f"      Sommet $sommet%2d: $proba%.4f (${proba * 100}%.2f%%)")
              }
              
            case None =>
              println("    Distribution stationnaire: NON TROUVÉE (pas de convergence)")
          }
      }
      
      println("-" * 70)
    }
    
    println()
  }
  
  /**
   * Analyse complète d'un graphe : CFC + distributions stationnaires
   * 
   * @param graphe graphe à analyser
   * @return partition du graphe
   */
  def analyseComplete(graphe: Graphe): Partition = {
    println("\n" + "=" * 70)
    println("ANALYSE DES COMPOSANTES FORTEMENT CONNEXES")
    println("=" * 70)
    
    // Détecter les CFC avec Tarjan
    val partition = tarjan(graphe)
    
    // Afficher la partition
    partition.afficher()
    
    // Afficher les distributions stationnaires
    afficherDistributionsStationnaires(graphe, partition)
    
    partition
  }
}
