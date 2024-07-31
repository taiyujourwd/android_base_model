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
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.Drawable
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
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
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
import com.benwu.baselib.dialog_fragment.BaseBottomSheetDialogFragment
import com.benwu.baselib.dialog_fragment.BaseDialogFragment
import com.benwu.baselib.extension.recyclerview.BaseGridLayoutManager
import com.benwu.baselib.extension.recyclerview.BaseLinearLayoutManager
import com.benwu.baselib.extension.recyclerview.SpaceItemDecoration
import com.benwu.baselib.extension.recyclerview.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Stack

//region view
/**
 * 創建MaterialShapeDrawable
 *
 * @param shapeAppearance 形狀
 * @param shapeAppearanceOverlay 覆蓋形狀
 * @param fillColor 填色
 * @param strokeWidth 框寬度
 * @param strokeColor 框顏色
 * @return MaterialShapeDrawable
 */
fun createMaterialShapeDrawable(
    context: Context,
    @StyleRes shapeAppearance: Int = com.google.android.material.R.style.ShapeAppearance_Material3_Corner_None,
    @StyleRes shapeAppearanceOverlay: Int = 0,
    @ColorInt fillColor: Int = Color.TRANSPARENT,
    strokeWidth: Float = 0f,
    @ColorInt strokeColor: Int = Color.TRANSPARENT
) = MaterialShapeDrawable(
    ShapeAppearanceModel.builder(context, shapeAppearance, shapeAppearanceOverlay).build()
).also {
    it.fillColor = ColorStateList.valueOf(fillColor)
    it.setStroke(strokeWidth.dp, strokeColor)
}

/**
 * 設置可裁剪背景
 *
 * @param drawable 背景
 */
fun View.setClipBackground(drawable: Drawable?) {
    background = drawable
    clipToOutline = true
}
//endregion

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
    action: (() -> Unit)? = null
) = also { toolbar ->
    toolbar.title = title
    toolbar.initMenu(menuRes)
    toolbar.setNavigationOnClickListener { action?.invoke() }
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
    filters = filters.toMutableList().also { it.add(newInputFilter) }.toTypedArray()
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
fun <T> ListAdapter<T, ViewHolder>.updateList(update: (ArrayList<T>) -> Unit) {
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
    @androidx.annotation.FloatRange(from = 0.0) spacing: Float = 0f,
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
    @androidx.annotation.FloatRange(from = 0.0) spacing: Float = 0f,
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

fun debugPrintV(tag: String, message: String) {
    if (IS_DEBUG) Log.v(tag, message)
}

fun debugPrintD(tag: String, message: String) {
    if (IS_DEBUG) Log.d(tag, message)
}

fun debugPrintI(tag: String, message: String) {
    if (IS_DEBUG) Log.i(tag, message)
}

fun debugPrintW(tag: String, message: String) {
    if (IS_DEBUG) Log.w(tag, message)
}

fun debugPrintE(tag: String, message: String) {
    if (IS_DEBUG) Log.e(tag, message)
}

fun Throwable.debugPrint() {
    if (IS_DEBUG) printStackTrace()
}
//endregion

//region toast/snackbar/dialog
/**
 * toast
 *
 * @param message 訊息
 * @param interval 顯示多長的時間
 */
fun showToast(context: Context, message: String, interval: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(context, message, interval).show()
}

/**
 * snackbar
 *
 * @param message 訊息
 * @param interval 顯示多長的時間
 * @param actionName 按鈕名稱
 */
fun showSnackbar(
    view: View,
    message: String,
    actionName: String? = null,
    interval: Int = Toast.LENGTH_SHORT,
    action: ((View) -> Unit)? = null
) {
    Snackbar.make(view, message, interval)
        .setAction(actionName) { action?.invoke(it) }
        .show()
}

/**
 * 提示視窗
 *
 * @param message 訊息
 * @param title 標題
 * @param isCancelable 是否可點擊外部取消
 */
fun showNotice(
    context: Context,
    message: String,
    title: String = context.getString(R.string.notice),
    positive: String = context.getString(R.string.sure),
    negative: String = "",
    neutral: String = "",
    isCancelable: Boolean = false,
    action: ((action: Int) -> Unit)? = null
) {
    val dialog = MaterialAlertDialogBuilder(context)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(isCancelable)
        .setPositiveButton(positive) { _, which -> action?.invoke(which) }

    if (!isNullOrEmpty(negative)) {
        dialog.setNegativeButton(negative) { _, which -> action?.invoke(which) }
    }

    if (!isNullOrEmpty(neutral)) {
        dialog.setNeutralButton(neutral) { _, which -> action?.invoke(which) }
    }

    dialog.show()
}

/**
 * 選擇器
 *
 * @param items 項目
 * @param title 標題
 */
fun showPicker(
    context: Context,
    items: Array<String>,
    title: String,
    select: (position: Int, data: String) -> Unit
) {
    MaterialAlertDialogBuilder(context)
        .setTitle(title)
        .setItems(items) { _, which -> select(which, items[which]) }
        .show()
}

/**
 * 單選
 *
 * @param items 項目
 * @param title 標題
 * @param selectItem 預設
 */
fun showSingleChoice(
    context: Context,
    items: Array<String>,
    title: String,
    selectItem: Int = 0,
    select: (position: Int, data: String) -> Unit
) {
    var position = 0

    MaterialAlertDialogBuilder(context)
        .setTitle(title)
        .setNegativeButton(R.string.cancel) { _, _ -> }
        .setPositiveButton(R.string.sure) { _, _ -> select(position, items[position]) }
        .setSingleChoiceItems(items, selectItem) { _, which -> position = which }
        .show()
}

/**
 * 複選
 *
 * @param items 項目
 * @param title 標題
 * @param selectItems 預設
 */
fun showMultiChoice(
    context: Context,
    items: Array<String>,
    title: String,
    selectItems: BooleanArray,
    select: (selectItems: BooleanArray) -> Unit
) {
    MaterialAlertDialogBuilder(context)
        .setTitle(title)
        .setNegativeButton(R.string.cancel) { _, _ -> }
        .setPositiveButton(R.string.sure) { _, _ -> select(selectItems) }
        .setMultiChoiceItems(items, selectItems) { _, which, checked ->
            selectItems[which] = checked
        }.show()
}

/**
 * 客製化視窗
 */
fun <T, V : ViewBinding> showDialogFragment(
    dialogFragment: BaseDialogFragment<T, V>,
    fragmentManager: FragmentManager,
    tag: String? = null,
    action: ((AppCompatDialogFragment, T?) -> Unit)? = null
) {
    dialogFragment
        .setOnDialogResultListener { dialog, data -> action?.invoke(dialog, data) }
        .show(fragmentManager, tag)
}

/**
 * 客製化底部視窗
 */
fun <T, V : ViewBinding> showBottomSheetDialogFragment(
    dialogFragment: BaseBottomSheetDialogFragment<T, V>,
    fragmentManager: FragmentManager,
    tag: String? = null,
    action: ((AppCompatDialogFragment, T?) -> Unit)? = null
) {
    dialogFragment
        .setOnDialogResultListener { dialog, data -> action?.invoke(dialog, data) }
        .show(fragmentManager, tag)
}
//endregion

//region api
/**
 * 處理api例外狀況
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T) = withContext(Dispatchers.IO) {
    try {
        ApiState.Success(apiCall())
    } catch (throwable: Throwable) {
        ApiState.Failure(throwable)
    }
}
//endregion

//region fragment
/**
 * 使用id找對應的fragment
 *
 * @return fragment
 */
inline fun <reified T> FragmentManager.findFragmentByIdT(id: Int): T? =
    when (val fragment = findFragmentById(id)) {
        is T -> fragment

        else -> null
    }

/**
 * 使用tag找對應的fragment
 *
 * @return fragment
 */
inline fun <reified T> FragmentManager.findFragmentByTagT(tag: String?): T? =
    when (val fragment = findFragmentByTag(tag)) {
        is T -> fragment

        else -> null
    }
//endregion

//region permission
/**
 * 是否授予權限
 *
 * @return 是/否
 */
fun isPermissionsGranted(context: Context, vararg permissions: String) = permissions.none {
    ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED
}

/**
 * 是否該向用戶解釋要求此權限的原因
 *
 * @return 是/否
 */
fun isPermissionRationale(
    activity: AppCompatActivity,
    vararg permissions: String
) = permissions.any {
    ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
}

/**
 * 權限遭拒提示視窗
 */
fun showPermissionDeniedNotice(context: Context, permissionName: String) {
    showNotice(
        context,
        context.getString(R.string.permission_denied, permissionName),
        positive = context.getString(R.string.setting),
        negative = context.getString(R.string.cancel)
    ) {
        if (it != DialogInterface.BUTTON_POSITIVE) return@showNotice
        openSetting(context)
    }
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
fun locationToCity(
    context: Context,
    lat: Double,
    lon: Double,
    success: (city: String) -> Unit
) {
    val geocoder = Geocoder(context, Locale.getDefault())

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        geocoder.getFromLocation(lat, lon, 1) {
            it.getOrNull(0)?.also { address ->
                if (isNullOrEmpty(address.adminArea)) {
                    success(address.subAdminArea)
                } else {
                    success(address.adminArea)
                }
            }
        }
    } else {
        geocoder.getFromLocation(lat, lon, 1)?.getOrNull(0)?.also {
            if (isNullOrEmpty(it.adminArea)) {
                success(it.subAdminArea)
            } else {
                success(it.adminArea)
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
fun locationToArea(
    context: Context,
    lat: Double,
    lon: Double,
    success: (area: String) -> Unit
) {
    val geocoder = Geocoder(context, Locale.getDefault())

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        geocoder.getFromLocation(lat, lon, 1) {
            it.getOrNull(0)?.also { address ->
                if (isNullOrEmpty(address.locality)) {
                    success(address.subLocality)
                } else {
                    success(address.locality)
                }
            }
        }
    } else {
        geocoder.getFromLocation(lat, lon, 1)?.getOrNull(0)?.also {
            if (isNullOrEmpty(it.locality)) {
                success(it.subLocality)
            } else {
                success(it.locality)
            }
        }
    }
}

/**
 * GPS是否開啟
 *
 * @return 是/否
 */
fun isGpsOpen(context: Context): Boolean {
    val manager = context.getSystemService(LocationManager::class.java)

    val isGpsOpen = manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    if (!isGpsOpen) {
        showNotice(
            context,
            context.getString(R.string.open_gps),
            positive = context.getString(R.string.setting),
            negative = context.getString(R.string.cancel)
        ) { which ->
            if (which != DialogInterface.BUTTON_POSITIVE) return@showNotice
            openActivity(context) { it.action = Settings.ACTION_LOCATION_SOURCE_SETTINGS }
        }
    }

    return isGpsOpen
}
//endregion

//region file
/**
 * 創建file
 *
 * @param fileName 檔案名稱
 * @return File
 */
fun File.createFile(fileName: String): File {
    if (!exists()) mkdir()
    return File(this, fileName).also { it.createNewFile() }
}

/**
 * 創建file
 *
 * @param fileName 檔案名稱
 * @return File
 */
fun File.createFile(fileName: String, input: InputStream): File {
    if (!exists()) mkdir()
    val file = File(this, fileName)
    val fos = file.outputStream()
    input.buffered().copyTo(fos)

    fos.flush()
    fos.close()
    input.close()

    return file
}

/**
 * bitmap轉file
 *
 * @param fileName 檔案名稱
 * @return File
 */
fun File.fromBitmap(fileName: String, bitmap: Bitmap): File {
    val file = createFile(fileName)
    val fos = file.outputStream()
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

    Files.walk(toPath())
        .sorted(Comparator.reverseOrder())
        .map { it.toFile() }
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

        val fos = file.outputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)

        fos.flush()
        fos.close()
    }
}

/**
 * 檔案下載
 *
 * @param path 路徑
 * @return File
 */
fun File.download(path: String) = runCatching {
    val url = URL(path)
    url.openConnection().connect()

    createFile(
        URLUtil.guessFileName(path, null, null),
        BufferedInputStream(url.openStream())
    )
}.getOrNull()

/**
 * 檔案下載
 *
 * @param url 路徑
 * @param fileName 檔案名稱
 */
fun download(
    context: Context,
    url: String,
    fileName: String,
    init: ((DownloadManager.Request) -> Unit)? = null
) {
    val request = DownloadManager.Request(Uri.parse(url))
        .setTitle(fileName)
        .setDescription(context.getString(R.string.downloading))
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

    init?.invoke(request)
    context.getSystemService(DownloadManager::class.java).enqueue(request)
    showToast(context, context.getString(R.string.download_notice))
}

/**
 * 檔案下載提示視窗
 *
 * @param url 路徑
 * @param fileName 檔案名稱
 */
fun downloadNotice(
    context: Context,
    url: String,
    fileName: String,
    init: ((DownloadManager.Request) -> Unit)? = null
) {
    showNotice(
        context,
        context.getString(R.string.download_msg, fileName),
        positive = context.getString(R.string.download),
        negative = context.getString(R.string.cancel)
    ) {
        if (it != DialogInterface.BUTTON_POSITIVE) return@showNotice
        download(context, url, fileName, init)
    }
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
fun initNotification(
    context: Context,
    title: String,
    content: String,
    @DrawableRes smallIconRes: Int,
    badge: Int = 0,
    intent: Intent? = null,
    init: ((builder: NotificationCompat.Builder) -> Unit)? = null
) = NotificationCompat.Builder(context, CHANNEL_ID).also {
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
                context,
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
fun sendNotification(
    context: Context,
    title: String,
    content: String,
    @DrawableRes smallIconRes: Int,
    badge: Int = 0,
    intent: Intent? = null,
    sound: Uri = Settings.System.DEFAULT_NOTIFICATION_URI,
    init: ((builder: NotificationCompat.Builder) -> Unit)? = null
) {
    val manager = NotificationManagerCompat.from(context)

    manager.createNotificationChannel(
        NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setName(CHANNEL_NAME)
            .setSound(sound, Notification.AUDIO_ATTRIBUTES_DEFAULT)
            .build()
    )

    if (isPermissionsGranted(context, Manifest.permission.POST_NOTIFICATIONS)) {
        manager.notify(
            NOTIFICATION_ID,
            initNotification(context, title, content, smallIconRes, badge, intent, init)
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
fun getIntentWithSingleTop(
    context: Context,
    clazz: Class<*>? = null,
    bundle: Bundle? = null
): Intent {
    val intent = clazz?.let {
        Intent(context, it).apply { flags = Intent.FLAG_ACTIVITY_SINGLE_TOP }
    } ?: Intent()

    bundle?.also { intent.putExtras(it) }

    return intent
}

/**
 * 導去指定頁
 */
fun openActivity(
    context: Context,
    clazz: Class<*>? = null,
    bundle: Bundle? = null,
    intent: ((Intent) -> Unit)? = null
) {
    context.startActivity(getIntentWithSingleTop(context, clazz, bundle).also {
        intent?.invoke(it)
    })
}

/**
 * 導去該網址
 *
 * @param toolbarTitle toolbar標題
 * @param url 網址
 * @param isWebView 是否使用webView
 */
fun openUrl(context: Context, toolbarTitle: String, url: String, isWebView: Boolean = IS_WEB_VIEW) {
    if (!isWebView) { // chrome
        openActivity(context) {
            it.action = Intent.ACTION_VIEW
            it.data = Uri.parse(url)
        }
    } else { // 內嵌網頁
        openActivity(
            context,
            WebViewActivity::class.java,
            bundleOf("title" to toolbarTitle, "data" to url)
        )
    }
}

/**
 * 導去設定頁
 */
fun openSetting(context: Context) {
    openActivity(context) {
        it.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        it.data = Uri.parse("package:${context.packageName}")
        it.addCategory(Intent.CATEGORY_DEFAULT)
    }
}

/**
 * 撥打電話
 */
fun callPhone(context: Context, tel: String) {
    openActivity(context) {
        it.action = Intent.ACTION_DIAL
        it.data = Uri.parse("tel:${tel}")
    }
}

/**
 * 導航至
 *
 * @param lat 緯度
 * @param lon 經度
 */
fun navigationTo(context: Context, lat: Double, lon: Double) {
    openActivity(context) {
        it.action = Intent.ACTION_VIEW
        it.data = Uri.parse("http://maps.google.com/maps?daddr=$lat,$lon")
        it.`package` = "com.google.android.apps.maps"
    }
}

/**
 * 搜尋地點
 *
 * @param place 地點
 */
fun searchPlace(context: Context, place: String) {
    openActivity(context) {
        it.action = Intent.ACTION_VIEW
        it.data = Uri.parse("geo:0,0?q=${place}")
        it.`package` = "com.google.android.apps.maps"
    }
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
fun getAttrValue(context: Context, @AttrRes attrRes: Int) = TypedValue().also {
    context.theme.resolveAttribute(attrRes, it, true)
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
fun getCertificates(sha1: String) {
    val sha1s = sha1.split(':')
    val data = ByteArray(sha1s.size)
    sha1s.indices.forEach { data[it] = sha1s[it].toInt(16).toByte() }
    debugPrintE("keyHash", data.toBase64() ?: "")
}

/**
 * 判斷是否為null/空
 *
 * @return 是/否
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