import java.util.Calendar


object GoogleTrendsUtil {
  private val URL = "https://trends.google.com/trends/api/dailytrends" +
    "?hl=%1$s&tz=%2$d&ed=%3$tY%3$tm%3$td&geo=%4$s"

  private val DefaultLocale = "en-US"
  private val DefaultTimeZone = -180
  private val DefaultLocation = "US"
  private val DefaultDaysAmount = 5

  private val RedundantBegin = 17
  private val RedundantEnd = 1

  case class RelatedQuery(
                           exploreLink: String,
                           query: String
                         )

  case class Image(
                    imageUrl: String,
                    newsUrl: String,
                    source: String
                  )

  case class Article(
                      image: Image,
                      snippet: String,
                      source: String,
                      timeAgo: String,
                      title: String,
                      url: String,
                    )

  case class TrendingSearch(
                             articles: Array[Article],
                             formattedTraffic: String,
                             image: Image,
                             relatedQueries: Array[RelatedQuery],
                             shareUrl: String,
                             title: RelatedQuery
                           )

  case class TrendingSearchesDay(
                                  date: String,
                                  formattedDate: String,
                                  trendingSearches: Array[TrendingSearch],
                                )

  case class Trend(
                    endDateForNextRequest: String,
                    rssFeedPageUrl: String,
                    trendingSearchesDays: TrendingSearchesDay,
                  )

  private def buildUrl(locale: String,
                       timeZone: Integer,
                       calendar: Calendar,
                       location: String): String = {

    String.format(URL, locale, timeZone, calendar, location)
  }


  def getTrend(locale: String,
               timeZone: Integer,
               calendar: Calendar,
               location: String): String = {
    val url = buildUrl(locale, timeZone, calendar, location)

    val content = scala.io.Source.fromURL(url).mkString

    removeRedundant(content)
  }

  def getLastTrends(locale: String,
                    timeZone: Integer,
                    location: String): List[String] = {
    val calendar = Calendar.getInstance()

    var list = List[String]()

    for (_ <- 1 to DefaultDaysAmount) {
      val trend: String = getTrend(locale, timeZone, calendar, location)

      list = trend :: list

      calendar.add(Calendar.DATE, -1)
    }

    list
  }

  private def removeRedundant(content: String) = {
    content.substring(RedundantBegin, content.length - RedundantEnd)
  }

}