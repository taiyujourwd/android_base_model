@file:Suppress("UNCHECKED_CAST")

package com.benwu.baselib.extension

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.InputFilter
import android.text.SpannableString
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import com.benwu.baselib.BuildConfig
import com.benwu.baselib.R
import com.benwu.baselib.activity.BaseActivity
import com.benwu.baselib.activity.WebViewActivity
import com.benwu.baselib.adapter.FragmentVpAdapter
import com.benwu.baselib.api.ApiState
import com.benwu.baselib.extension.recyclerview.BaseGridLayoutManager
import com.benwu.baselib.extension.recyclerview.BaseLinearLayoutManager
import com.benwu.baselib.extension.recyclerview.SpaceItemDecoration
import com.benwu.baselib.extension.recyclerview.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Stack

//region bar
/**
 * 初始化toolbar
 *
 * @param title 標題
 * @return toolbar
 */
fun MaterialToolbar.init(
    title: String? = null,
    @MenuRes menuRes: Int = 0,
    onNavigationClick: (() -> Unit)? = null
) = also { toolbar ->
    toolbar.title = title
    toolbar.initMenu(menuRes)
    if (isNullOrEmpty(onNavigationClick)) return@also
    toolbar.setNavigationOnClickListener { onNavigationClick?.invoke() }
}

/**
 * 初始化toolbar
 *
 * @param title 標題
 * @return toolbar
 */
fun MaterialToolbar.init(
    activity: BaseActivity<*>,
    title: String? = null,
    @MenuRes menuRes: Int = 0
) = init(title, menuRes) {
    activity.onBack()
}

/**
 * 初始化toolbarMenu
 *
 * @return toolbar
 */
fun MaterialToolbar.initMenu(@MenuRes menuRes: Int) = also {
    it.menu.clear()
    if (menuRes != 0) it.inflateMenu(menuRes)
}
//endregion

//region editText
/**
 * 新增輸入限制
 *
 * @param newInputFilter 輸入限制
 */
fun TextInputEditText.addFilter(newInputFilter: InputFilter) {
    val mFilters = arrayOfNulls<InputFilter>(filters.size + 1)

    filters.forEachIndexed<InputFilter?> { i: Int, inputFilter: InputFilter? ->
        if (!isNullOrEmpty(inputFilter)) mFilters[i] = inputFilter
    }

    mFilters[filters.size] = newInputFilter
    filters = mFilters
}

/**
 * 關閉鍵盤
 */
fun Context.hideKeyboard(view: View?) {
    val manager = getSystemService(InputMethodManager::class.java)
    view?.also { manager.hideSoftInputFromWindow(it.windowToken, 0) }
}
//endregion

//region adapter
/**
 * 更新項目
 */
fun <T, V : ViewBinding> ListAdapter<T, ViewHolder<V>>.updateList(update: (ArrayList<T>) -> Unit) {
    submitList(ArrayList(currentList).also { update(it) })
}
//endregion

//region recyclerView
/**
 * 初始化recyclerView
 *
 * @param spacing 間距
 * @param orientation 縱/橫向
 * @param spanCount 一列有多少項(grid)
 * @return recyclerView
 */
private fun RecyclerView.init(
    layoutManager: RecyclerView.LayoutManager,
    adapter: RecyclerView.Adapter<*>,
    spacing: Float,
    orientation: Int,
    spanCount: Int = 0
) = also {
    it.layoutManager = layoutManager
    it.adapter = adapter

    for (i in 0 until it.itemDecorationCount) it.removeItemDecorationAt(i) // 防止有多餘的itemDecoration
    if (spacing == 0f) return@also
    it.addItemDecoration(SpaceItemDecoration(spacing.dp.toInt(), orientation, spanCount))
}

/**
 * 初始化recyclerView(linear)
 *
 * @param spacing 間距
 * @param orientation 縱/橫向
 * @return recyclerView
 */
fun RecyclerView.init(
    adapter: RecyclerView.Adapter<*>,
    spacing: Float = 0f,
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL
) = init(BaseLinearLayoutManager(context, orientation), adapter, spacing, orientation)

/**
 * 初始化recyclerView(grid)
 *
 * @param spanCount 一列有多少項(grid)
 * @param spacing 間距
 * @param orientation 縱/橫向
 * @return recyclerView
 */
fun RecyclerView.init(
    @androidx.annotation.IntRange(from = 2) spanCount: Int,
    adapter: RecyclerView.Adapter<*>,
    spacing: Float = 0f,
    @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL
) = init(
    BaseGridLayoutManager(context, spanCount, orientation), adapter, spacing, orientation, spanCount
)
//endregion

//region viewPager
/**
 * 初始化viewPager
 *
 * @param pageTransformer 滑動效果
 * @param orientation 縱/橫向
 * @param direction 滑動方向
 * @return viewPager
 */
fun ViewPager2.init(
    adapter: RecyclerView.Adapter<*>,
    pageTransformer: ViewPager2.PageTransformer? = null,
    @ViewPager2.Orientation orientation: Int = ViewPager2.ORIENTATION_HORIZONTAL,
    direction: Int = ViewPager2.LAYOUT_DIRECTION_LTR
) = also { vp ->
    vp.adapter = adapter
    pageTransformer?.also { setPageTransformer(it) }
    vp.orientation = orientation
    vp.layoutDirection = direction
}

/**
 * 初始化viewPager + fragment
 *
 * @param orientation 縱/橫向
 * @param direction 滑動方向
 * @return viewPager
 */
private fun ViewPager2.init(
    adapter: RecyclerView.Adapter<*>,
    offscreenPageLimit: Int,
    orientation: Int,
    direction: Int
) = also {
    it.adapter = adapter
    it.offscreenPageLimit = offscreenPageLimit
    it.orientation = orientation
    it.layoutDirection = direction
}

/**
 * 初始化viewPager + fragment(activity)
 *
 * @param orientation 縱/橫向
 * @param direction 滑動方向
 * @return viewPager
 */
fun ViewPager2.init(
    activity: AppCompatActivity,
    fragmentList: List<Fragment>,
    @ViewPager2.Orientation orientation: Int = ViewPager2.ORIENTATION_HORIZONTAL,
    direction: Int = ViewPager2.LAYOUT_DIRECTION_LTR
) = init(FragmentVpAdapter(activity, fragmentList), fragmentList.size, orientation, direction)

/**
 * 初始化viewPager + fragment(fragment)
 *
 * @param orientation 縱/橫向
 * @param direction 滑動方向
 * @return viewPager
 */
fun ViewPager2.init(
    fragment: Fragment,
    fragmentList: List<Fragment>,
    @ViewPager2.Orientation orientation: Int = ViewPager2.ORIENTATION_HORIZONTAL,
    direction: Int = ViewPager2.LAYOUT_DIRECTION_LTR
) = init(FragmentVpAdapter(fragment, fragmentList), fragmentList.size, orientation, direction)
//endregion

//region tab
/**
 * 初始化tab
 *
 * @param currentItem 目前item
 * @return tabLayout
 */
fun TabLayout.init(
    viewPager: ViewPager2,
    @androidx.annotation.IntRange(from = 0) currentItem: Int = 0,
    init: (tab: TabLayout.Tab, position: Int) -> Unit
) = also {
    TabLayoutMediator(it, viewPager) { tab, position -> init(tab, position) }.attach()
    viewPager.currentItem = currentItem
}

/**
 * 初始化tab(string)
 *
 * @param titles 標題
 * @param currentItem 目前item
 * @return tabLayout
 */
fun TabLayout.init(
    viewPager: ViewPager2,
    titles: Array<String>,
    @androidx.annotation.IntRange(from = 0) currentItem: Int = 0
) = init(viewPager, currentItem) { tab, position -> tab.text = titles[position] }
//endregion

// region webView
/**
 * 初始化webView
 *
 * @return webView
 */
@SuppressLint("SetJavaScriptEnabled")
fun WebView.init(setting: ((settings: WebSettings) -> Unit)? = null) = also {
    it.setLayerType(View.LAYER_TYPE_HARDWARE, null)

    it.clearCache(true)
    it.clearFormData()
    it.clearHistory()

    it.settings.javaScriptEnabled = true // 支援js
    it.settings.javaScriptCanOpenWindowsAutomatically = true // 支援js打開新視窗
    it.settings.allowFileAccess = true // 支援訪問文件
    it.settings.loadsImagesAutomatically = true // 支援自動加載圖片
    it.settings.defaultTextEncodingName = "utf-8" // 編碼格式
    it.settings.domStorageEnabled = true

    // 自適應螢幕
    it.settings.useWideViewPort = true
    it.settings.loadWithOverviewMode = true

    // 缩放
    it.settings.setSupportZoom(false)
    it.settings.builtInZoomControls = false
    it.settings.displayZoomControls = false

    setting?.invoke(settings)
}

/**
 * 讀取html
 *
 * @param baseUrl 網域
 * @param content 內容
 */
fun WebView.loadHtml(baseUrl: String, content: String) {
    val data = StringBuilder()

    data.append("<html>\n").append("<head>\n")
        .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
        .append("<style>\n").append("img {\n").append("  width: 100%;").append("  height: auto;")
        .append("}\n").append("</style>\n").append("</head>\n")
        .append(String.format("<body>%s</body></html>", content))

    loadDataWithBaseURL(baseUrl, data.toString(), "text/html", "utf-8", null)
}
//endregion

//region imageView
/**
 * 圖片
 *
 * @param url 圖片路徑
 * @param placeholderRes 預設圖
 */
fun AppCompatImageView.loadImage(url: Any?, @DrawableRes placeholderRes: Int) {
    if (placeholderRes == 0) return

    Glide.with(context).asBitmap()
        .apply(RequestOptions().placeholder(placeholderRes).error(placeholderRes)).load(url)
        .into(this)
}

/**
 * 動圖
 *
 * @param url 動圖路徑
 * @param placeholderRes 預設圖
 */
fun AppCompatImageView.loadGIF(url: Any?, @DrawableRes placeholderRes: Int) {
    if (placeholderRes == 0) return

    Glide.with(context).asGif()
        .apply(RequestOptions().placeholder(placeholderRes).error(placeholderRes)).load(url)
        .into(this)
}
//endregion

//region list/array
/**
 * 條件新增
 */
fun <T> MutableList<T>.addIf(data: T, condition: () -> Boolean) {
    if (condition()) add(data)
}

/**
 * 條件移除
 */
fun <T> MutableList<T>.removeIf(data: T, condition: () -> Boolean) {
    if (condition()) remove(data)
}

/**
 * 條件移除
 *
 * @param position 移除位置
 */
fun <T> MutableList<T>.removeIf(position: Int, condition: () -> Boolean) {
    if (condition()) removeAt(position)
}

/**
 * 將dataList刷新
 */
fun <T> MutableList<T>.refresh(dataList: List<T>) {
    clear()
    addAll(dataList)
}

/**
 * 關鍵字查詢
 *
 * @param keyword 關鍵字
 * @param ignoreCase 是否忽略大小寫
 * @param fields 搜尋欄位
 * @return 關鍵字查詢(dataList)
 */
fun <T> Iterable<T>.search(
    keyword: String?,
    ignoreCase: Boolean = true,
    fields: (T) -> Iterable<String?>
) = filter { data ->
    fields(data).any { it?.contains(keyword ?: "", ignoreCase) ?: false }
}

/**
 * 依據
 *
 * @return 依據(dataList)
 */
fun <T, D> Iterable<T>.listBy(data: (T) -> D) = ArrayList<D>().also { dataList ->
    forEach { dataList.add(data(it)) }
}
//endregion

//region string
/**
 * string樣式
 *
 * @param spans 樣式
 * @return string
 */
fun CharSequence.setSpan(vararg spans: Any) = SpannableString(this).also { string ->
    spans.forEach { string.setSpan(it, 0, length, 0) }
}
//endregion

//region dateTime
/**
 * string轉date
 *
 * @param pattern 格式
 * @return date
 */
fun String.parseDate(pattern: String) = runCatching {
    SimpleDateFormat(pattern, Locale.getDefault()).parse(this)
}.getOrNull()

/**
 * date轉string
 *
 * @param pattern 格式
 * @return string
 */
fun Date.formatString(pattern: String) = runCatching {
    SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}.getOrNull()

/**
 * long轉string
 *
 * @param pattern 格式
 * @return string
 */
fun Long.formatString(pattern: String) = Date(this).formatString(pattern)
//endregion

//region log
val IS_DEBUG = BuildConfig.DEBUG

fun String.v(tag: String) {
    if (IS_DEBUG) Log.v(tag, this)
}

fun String.d(tag: String) {
    if (IS_DEBUG) Log.d(tag, this)
}

fun String.i(tag: String) {
    if (IS_DEBUG) Log.i(tag, this)
}

fun String.w(tag: String) {
    if (IS_DEBUG) Log.w(tag, this)
}

fun String.e(tag: String) {
    if (IS_DEBUG) Log.e(tag, this)
}

fun Throwable.print() {
    if (IS_DEBUG) printStackTrace()
}
//endregion

//region toast/snackbar/dialog
/**
 * toast
 *
 * @param interval 顯示多長的時間
 * @return toast
 */
fun String.toast(context: Context, interval: Int = Toast.LENGTH_SHORT): Toast =
    Toast.makeText(context, this, interval)

/**
 * snackbar
 *
 * @param interval 顯示多長的時間
 * @return snackbar
 */
fun String.snackbar(view: View, interval: Int = Snackbar.LENGTH_SHORT): Snackbar =
    Snackbar.make(view, this, interval)

/**
 * 訊息
 *
 * @param title 標題
 * @param isCancelable 是否可點擊外部取消
 * @return dialog
 */
fun String.message(
    context: Context,
    negative: String = "",
    neutral: String = "",
    positive: String = context.getString(R.string.sure),
    title: String = context.getString(R.string.notice),
    isCancelable: Boolean = false,
    onClick: ((action: Int) -> Unit)? = null
) = MaterialAlertDialogBuilder(context).also {
    it.setTitle(title).setMessage(this).setCancelable(isCancelable)
    it.setPositiveButton(positive) { _, which -> onClick?.invoke(which) }
    if (!isNullOrEmpty(negative)) it.setNegativeButton(negative) { _, which -> onClick?.invoke(which) }
    if (!isNullOrEmpty(neutral)) it.setNeutralButton(neutral) { _, which -> onClick?.invoke(which) }
}

/**
 * 選擇器
 *
 * @param title 標題
 * @return dialog
 */
fun Array<String>.picker(
    context: Context,
    title: String,
    onSelect: (position: Int, data: String) -> Unit
) = MaterialAlertDialogBuilder(context).setTitle(title)
    .setItems(this) { _, which -> onSelect(which, get(which)) }

/**
 * 單選
 *
 * @param title 標題
 * @param selectItem 預設選取
 * @return dialog
 */
fun Array<String>.singleChoice(
    context: Context,
    title: String,
    selectItem: Int = 0,
    onSelect: (position: Int, data: String) -> Unit
) = MaterialAlertDialogBuilder(context).also {
    var position = 0

    it.setTitle(title).setNegativeButton(R.string.cancel) { _, _ -> }
        .setPositiveButton(R.string.sure) { _, _ -> onSelect(position, get(position)) }
        .setSingleChoiceItems(this, selectItem) { _, which -> position = which }
}

/**
 * 複選
 *
 * @param title 標題
 * @param selectItems 預設選取
 * @return dialog
 */
fun Array<String>.multiChoice(
    context: Context,
    title: String,
    selectItems: BooleanArray,
    onSelect: (selectItems: BooleanArray) -> Unit
) = MaterialAlertDialogBuilder(context).setTitle(title)
    .setNegativeButton(R.string.cancel) { _, _ -> }
    .setPositiveButton(R.string.sure) { _, _ -> onSelect(selectItems) }
    .setMultiChoiceItems(this, selectItems) { _, which, checked -> selectItems[which] = checked }
//endregion

//region api
/**
 * 處理api例外狀況
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): ApiState<T> {
    return withContext(Dispatchers.IO) {
        try {
            ApiState.Success(apiCall())
        } catch (throwable: Throwable) {
            ApiState.Failure(throwable)
        }
    }
}
//endregion

//region fragment
fun <T> FragmentManager.findFragmentByTagT(tag: String?) = findFragmentByTag(tag) as T

fun <T> FragmentManager.findFragmentByIdT(id: Int) = findFragmentById(id) as T
//endregion

//region permission
/**
 * 是否授予權限
 *
 * @return 是/否
 */
fun Context.isPermissionsGranted(vararg permissions: String) = permissions.none {
    ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_DENIED
}

/**
 * 是否該向用戶解釋要求此權限原因
 *
 * @return 是/否
 */
fun AppCompatActivity.isPermissionRationale(vararg permissions: String) = permissions.any {
    ActivityCompat.shouldShowRequestPermissionRationale(this, it)
}

/**
 * 權限遭拒訊息
 *
 * @return 權限遭拒訊息
 */
fun Context.permissionDeniedMsg(permissionsName: String) =
    getString(R.string.permission_denied, permissionsName).message(
        this,
        getString(R.string.cancel),
        positive = getString(R.string.setting)
    ) {
        if (it != DialogInterface.BUTTON_POSITIVE) return@message
        openSetting()
    }
//endregion

//region location
/**
 * 計算距離
 *
 * @param lat 緯度
 * @param lon 經度
 * @return 距離
 */
fun Location.distanceTo(lat: Double, lon: Double) = FloatArray(1).also {
    Location.distanceBetween(latitude, longitude, lat, lon, it)
}[0].toInt()

/**
 * 經緯度轉縣市
 *
 * @param lat 緯度
 * @param lon 經度
 * @return 縣市
 */
fun Context.locationToCity(lat: Double, lon: Double, onSuccess: (area: String) -> Unit) {
    val geocoder = Geocoder(this, Locale.getDefault())

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        geocoder.getFromLocation(lat, lon, 1) {
            it.getOrNull(0)?.also { address ->
                if (isNullOrEmpty(address.adminArea)) {
                    onSuccess(address.subAdminArea)
                } else {
                    onSuccess(address.adminArea)
                }
            }
        }
    } else {
        geocoder.getFromLocation(lat, lon, 1)?.getOrNull(0)?.also {
            if (isNullOrEmpty(it.adminArea)) {
                onSuccess(it.subAdminArea)
            } else {
                onSuccess(it.adminArea)
            }
        }
    }
}

/**
 * 經緯度轉地區
 *
 * @param lat 緯度
 * @param lon 經度
 * @return 地區
 */
fun Context.locationToArea(lat: Double, lon: Double, onSuccess: (area: String) -> Unit) {
    val geocoder = Geocoder(this, Locale.getDefault())

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        geocoder.getFromLocation(lat, lon, 1) {
            it.getOrNull(0)?.also { address ->
                if (isNullOrEmpty(address.locality)) {
                    onSuccess(address.subLocality)
                } else {
                    onSuccess(address.locality)
                }
            }
        }
    } else {
        geocoder.getFromLocation(lat, lon, 1)?.getOrNull(0)?.also {
            if (isNullOrEmpty(it.locality)) {
                onSuccess(it.subLocality)
            } else {
                onSuccess(it.locality)
            }
        }
    }
}

/**
 * GPS是否開啟
 *
 * @return 是否開啟
 */
fun Context.isGpsOpen(): Boolean {
    val manager = getSystemService(LocationManager::class.java)

    val isGpsOpen =
        manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    if (!isGpsOpen) {
        getString(R.string.open_gps).message(
            this,
            getString(R.string.cancel),
            positive = getString(R.string.setting)
        ) { type ->
            if (type != DialogInterface.BUTTON_POSITIVE) return@message
            startActivity(getIntentWithSingleTop().also {
                it.action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
            })
        }.show()
    }

    return isGpsOpen
}
//endregion

//region file
/**
 * 創建file
 *
 * @param fileName 檔案名稱
 */
fun File.createFile(fileName: String): File {
    if (!exists()) mkdir()
    return File(this, fileName).also { it.createNewFile() }
}

/**
 * 創建file
 *
 * @param fileName 檔案名稱
 */
fun File.createFile(fileName: String, input: InputStream): File {
    if (!exists()) mkdir()
    val file = File(this, fileName)
    input.buffered().copyTo(file.outputStream())
    input.close()

    return file
}

/**
 * bitmap轉file
 *
 * @param fileName 檔案名稱
 * @return file
 */
fun File.fromBitmap(fileName: String, bitmap: Bitmap): File {
    val file = createFile(fileName)
    val fos = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
    fos.flush()
    fos.close()
    return file
}

/**
 * 刪除主目錄
 */
fun File.deleteDir() {
    if (!exists()) return

    Files.walk(toPath()).sorted(Comparator.reverseOrder()).map { it.toFile() }
        .forEach { it.delete() }
}

/**
 * 矯正圖片角度
 */
suspend fun File.correctImageRotation() = withContext(Dispatchers.IO) {
    return@withContext this@correctImageRotation.also { file ->
        var bitmap = BitmapFactory.decodeFile(absolutePath)
        val exifInterface = ExifInterface(absolutePath)

        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val rotation = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> {
                90f
            }

            ExifInterface.ORIENTATION_ROTATE_180 -> {
                180f
            }

            ExifInterface.ORIENTATION_ROTATE_270 -> {
                270f
            }

            else -> {
                0f
            }
        }

        bitmap = Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.getWidth(),
            bitmap.getHeight(),
            Matrix().also { it.postRotate(rotation) },
            true
        )

        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.flush()
        fos.close()
    }
}

/**
 * 檔案下載
 *
 * @param path 路徑
 */
fun File.download(path: String): File {
    val url = URL(path)
    url.openConnection().connect()

    return createFile(
        URLUtil.guessFileName(path, null, null),
        BufferedInputStream(url.openStream())
    )
}

/**
 * 檔案下載
 *
 * @param url 路徑
 * @param fileName 檔案名稱
 */
fun Context.download(
    url: String,
    fileName: String,
    init: ((DownloadManager.Request) -> Unit)? = null
) {
    val request = DownloadManager.Request(Uri.parse(url)).setTitle(fileName)
        .setDescription(getString(R.string.downloading))
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

    init?.invoke(request)
    getSystemService(DownloadManager::class.java).enqueue(request)
    getString(R.string.download_notice).toast(this).show()
}

/**
 * 檔案下載(dialog)
 *
 * @param url 路徑
 * @param fileName 檔案名稱
 */
fun Context.downloadDialog(
    url: String,
    fileName: String,
    init: ((DownloadManager.Request) -> Unit)? = null
) = getString(R.string.download_msg, fileName).message(
    this,
    getString(R.string.cancel),
    positive = getString(R.string.download)
) {
    if (it != DialogInterface.BUTTON_POSITIVE) return@message
    download(url, fileName, init)
}
//endregion

//region 推播
const val CHANNEL_ID = "channelID"
const val CHANNEL_NAME = "channelName"
const val NOTIFICATION_ID = 65536

/**
 * 初始化推播
 *
 * @param title 標題
 * @param content 內容
 * @param smallIconRes 小icon
 * @param badge 數量
 * @return 推播
 */
fun Context.initNotification(
    title: String,
    content: String,
    @DrawableRes smallIconRes: Int,
    badge: Int = 0,
    intent: Intent? = null,
    init: ((builder: NotificationCompat.Builder) -> Unit)? = null
): Notification = NotificationCompat.Builder(this, CHANNEL_ID).also {
    it.setContentTitle(title)
        .setContentText(content)
        .setSmallIcon(smallIconRes)
        .setNumber(badge)
        .priority = NotificationCompat.PRIORITY_DEFAULT

    // 點擊跳頁
    intent?.also { intent ->
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

        it.setContentIntent(
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        ).setAutoCancel(true)
    }

    init?.invoke(it)
}.build()

/**
 * 發送推播
 *
 * @param title 標題
 * @param content 內容
 * @param smallIconRes 小icon
 * @param badge 數量
 * @param sound 音效
 */
@SuppressLint("MissingPermission")
fun Context.sendNotification(
    title: String,
    content: String,
    @DrawableRes smallIconRes: Int,
    badge: Int = 0,
    intent: Intent? = null,
    sound: Uri = Settings.System.DEFAULT_NOTIFICATION_URI,
    init: ((builder: NotificationCompat.Builder) -> Unit)? = null
) {
    val manager = NotificationManagerCompat.from(this)

    manager.createNotificationChannel(
        NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setName(CHANNEL_NAME)
            .setSound(sound, Notification.AUDIO_ATTRIBUTES_DEFAULT)
            .build()
    )

    if (isPermissionsGranted(Manifest.permission.POST_NOTIFICATIONS)) {
        manager.notify(
            NOTIFICATION_ID,
            initNotification(title, content, smallIconRes, badge, intent, init)
        )
    }
}
//endregion

//region 導去指定頁/返回上一頁
private const val IS_WEB_VIEW = true // 是否使用webView

/**
 * 取得intent
 *
 * @return intent
 */
fun Context.getIntentWithSingleTop(clazz: Class<*>? = null, bundle: Bundle? = null): Intent {
    val intent = clazz?.let {
        Intent(this, it).apply { flags = Intent.FLAG_ACTIVITY_SINGLE_TOP }
    } ?: Intent()

    bundle?.also { intent.putExtras(it) }

    return intent
}

/**
 * 導去指定頁
 */
fun Context.openActivity(clazz: Class<*>? = null, bundle: Bundle? = null) {
    startActivity(getIntentWithSingleTop(clazz, bundle))
}

/**
 * 導去該網址
 *
 * @param toolbarTitle toolbar標題
 * @param url 網址
 * @param isWebView 是否使用webView
 */
fun Context.openUrl(toolbarTitle: String, url: String, isWebView: Boolean = IS_WEB_VIEW) {
    if (!isWebView) { // chrome
        startActivity(getIntentWithSingleTop().also {
            it.action = Intent.ACTION_VIEW
            it.data = Uri.parse(url)
        })
    } else { // 內嵌網頁
        openActivity(WebViewActivity::class.java, bundleOf("title" to toolbarTitle, "data" to url))
    }
}

/**
 * 導去設定頁
 */
fun Context.openSetting() {
    startActivity(getIntentWithSingleTop().also {
        it.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        it.data = Uri.parse("package:$packageName")
        it.addCategory(Intent.CATEGORY_DEFAULT)
    })
}

/**
 * 撥打電話
 */
fun Context.callPhone(tel: String) {
    startActivity(getIntentWithSingleTop().also {
        it.action = Intent.ACTION_DIAL
        it.data = Uri.parse("tel:${tel}")
    })
}

/**
 * 導航至
 *
 * @param lat 緯度
 * @param lon 經度
 */
fun Context.navigationTo(lat: Double, lon: Double) {
    startActivity(getIntentWithSingleTop().also {
        it.action = Intent.ACTION_VIEW
        it.data = Uri.parse("http://maps.google.com/maps?daddr=$lat,$lon")
        it.`package` = "com.google.android.apps.maps"
    })
}

/**
 * 搜尋位置
 *
 * @param location 位置
 */
fun Context.searchLocation(location: String) {
    startActivity(getIntentWithSingleTop().also {
        it.action = Intent.ACTION_VIEW
        it.data = Uri.parse("geo:0,0?q=${location}")
        it.`package` = "com.google.android.apps.maps"
    })
}
//endregion

//region 額外方法
/**
 * byte轉base64
 *
 * @return base64
 */
fun ByteArray.toBase64() = runCatching {
    Base64.encodeToString(this, Base64.DEFAULT)
}.getOrNull()

/**
 * 取得attr值
 *
 * @return attr值
 */
fun Context.getAttrValue(@AttrRes attrRes: Int) = TypedValue().also {
    theme.resolveAttribute(attrRes, it, true)
}.data

/**
 * dp to px
 */
val Float.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )

/**
 * sp to px
 */
val Float.sp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        Resources.getSystem().displayMetrics
    )

/**
 * 產生28個字元的密鑰雜湊
 */
fun String.getCertificates() {
    val sha1s = split(':')
    val data = ByteArray(sha1s.size)
    sha1s.indices.forEach { data[it] = sha1s[it].toInt(16).toByte() }
    "keyHash".e(Base64.encodeToString(data, Base64.DEFAULT))
}

/**
 * 判斷是否為null/空
 *
 * @return 是否為null/空
 */
fun isNullOrEmpty(vararg data: Any?): Boolean {
    data.forEach {
        if (it == null) return true
        if (it is Stack<*> && it.isEmpty()) return true // stack
        if (it is Array<*> && it.isEmpty()) return true // array
        if (it is List<*> && it.isEmpty()) return true // list
        if (it is Map<*, *> && it.isEmpty()) return true // map
        if (it is CharSequence && it.isBlank()) return true // charSequence
    }

    return false
}
//endregion